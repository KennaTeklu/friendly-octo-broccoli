package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.File
import java.util.UUID

/**
 * Profiles — full per-profile data isolation (PF P4.Profiles L58572–58728).
 * Each profile owns a Room namespace: the legacy "pro.db" file stays the
 * default profile's store (additive migration — existing users keep everything),
 * other profiles get pro_<id>.db. Switching is live: flush → reopen store →
 * loadAll → notify (no restart).
 *
 * BATCH-4B item 4: "default" is no longer always the hardcoded `DEFAULT` ID —
 * the user can promote any profile to be the cold-start profile via
 * `setAsDefault`. The default profile is delete-protected.
 *
 * BATCH-4B item 5: creating a profile now seeds that profile's Room `user`
 * table with `user.name = <profile name>` so Personal Info reads the right
 * name from the moment the profile is opened.
 */
object Profiles {

    const val DEFAULT = "default"
    const val ACTIVE_KEY = "p4_active_profile"
    /** BATCH-4B item 4: which profile the app opens to on cold start. */
    const val DEFAULT_PROFILE_KEY = "p4_default_profile"

    data class ProfileMeta(val id: String, val name: String, val emoji: String?)

    fun activeId(ctx: Context): String =
        ProPrefs.get(ctx, ACTIVE_KEY)?.takeIf { it.isNotBlank() } ?: DEFAULT

    /** BATCH-4B item 4: which profile opens on cold start. Defaults to `DEFAULT`. */
    fun defaultId(ctx: Context): String =
        ProPrefs.get(ctx, DEFAULT_PROFILE_KEY)?.takeIf { it.isNotBlank() } ?: DEFAULT

    /** BATCH-4B item 4: promote a profile to be the cold-start default. */
    fun setAsDefault(ctx: Context, profileId: String) {
        val id = if (profileId.isBlank()) DEFAULT else profileId
        ProPrefs.put(ctx, DEFAULT_PROFILE_KEY, id)
        ProLog.i("PROFILE", "default set to $id")
    }

    fun dbNameFor(ctx: Context, profileId: String): String =
        if (profileId == DEFAULT) "pro.db" else "pro_$profileId.db"

    fun list(ctx: Context): List<ProfileMeta> {
        val (profiles, _, _) = ProfileRegistry.read(ctx)
        val base = profiles.map { ProfileMeta(it.id, it.name, it.emoji) }
        return if (base.isEmpty()) listOf(ProfileMeta(DEFAULT, "Default", "🏋️")) else base
    }

    /**
     * BATCH-4B item 4: display label format `Name (Default) (Currently selected)`.
     *  - `Name` = the profile's own live user.name (from Personal Info) — falls
     *    back to the registry name, then to "Default" — never hardcoded beyond
     *    that one neutral fallback word.
     *  - `(Default)` appended only on the default profile.
     *  - `(Currently selected)` appended only on the active profile.
     *
     * Examples:
     *   - active default profile named "Alex" → "Alex (Default) (Currently selected)"
     *   - non-active non-default profile named "Sam" → "Sam"
     *   - default profile with no name, active → "Default (Currently selected)"
     *   - default profile with no name, not active → "Default"
     */
    fun displayLabel(ctx: Context, profileId: String): String {
        val registryName = list(ctx).firstOrNull { it.id == profileId }?.name
        val liveName = liveNameFor(ctx, profileId)
        val name = liveName.ifBlank { registryName?.takeIf { it.isNotBlank() } ?: "Default" }
        val isDefault = profileId == defaultId(ctx)
        val isActive = profileId == activeId(ctx)
        val parts = mutableListOf(name)
        if (isDefault) parts += "(Default)"
        if (isActive) parts += "(Currently selected)"
        return parts.joinToString(" ")
    }

    /**
     * Live user.name for a profile, pulled from that profile's Room `user`
     * table. Returns "" if the profile has no user row or the name is blank.
     * Opens the profile's Room DB directly so the call works for any profile,
     * not just the active one.
     */
    fun liveNameFor(ctx: Context, profileId: String): String = try {
        ProDb.withDb(ctx, dbNameFor(ctx, profileId)) { dao ->
            kotlinx.coroutines.runBlocking {
                val raw = dao.userJson() ?: return@runBlocking ""
                try {
                    val u = ProJson.decode(UserProfile.serializer(), raw)
                    u.name
                } catch (_: Exception) { "" }
            }
        }
    } catch (_: Exception) { "" }

    fun create(ctx: Context, name: String, emoji: String? = null): ProfileMeta {
        val id = "p_${UUID.randomUUID().toString().take(8)}"
        val emojiFinal = emoji ?: randomAnimalEmoji(ctx)
        val (profiles, _, ask) = ProfileRegistry.read(ctx)
        val newProfile = ProfileRegistry.Profile(id, name, emojiFinal, System.currentTimeMillis(), profiles.size)
        ProfileRegistry.write(ctx, profiles + newProfile, activeId(ctx), ask)
        // BATCH-4B item 5: seed the new profile's Room `user` table with
        // user.name = <profile name> so Personal Info reads the right name the
        // moment the new profile is opened. Other user fields stay at defaults.
        seedNewProfileUser(ctx, id, name)
        ProLog.i("PROFILE", "created $id ($name)")
        return ProfileMeta(id, name, emojiFinal)
    }

    /** BATCH-4B item 5: seed the new profile's user.name = <profile name>.
     *  P4D-CRASH-02 (W3): uses ProDb.withDb() so the handle survives concurrent
     *  close(). */
    private fun seedNewProfileUser(ctx: Context, profileId: String, name: String) {
        try {
            val defaultUser = UserProfile(
                created = ProState.nowIso(),
                name = name,
                experience = "intermediate",
                settings = Settings(darkMode = false),
            )
            ProDb.withDb(ctx, dbNameFor(ctx, profileId)) { dao ->
                kotlinx.coroutines.runBlocking {
                    dao.putUser(UserRow("main", ProJson.encode(UserProfile.serializer(), defaultUser)))
                }
            }
        } catch (e: Exception) {
            ProLog.w("PROFILE", "seed user failed for $profileId: ${e.message}")
        }
    }

    fun delete(ctx: Context, profileId: String) {
        if (profileId == DEFAULT) return
        if (profileId == defaultId(ctx)) {
            ProLog.w("PROFILE", "delete blocked — $profileId is the default; reassign default first")
            return
        }
        val (profiles, _, ask) = ProfileRegistry.read(ctx)
        val remaining = profiles.filter { it.id != profileId }
        val wasActive = activeId(ctx) == profileId
        // BATCH-5A: pick a new active whose DB actually exists (not just the
        // first remaining profile, which may have been created but never opened).
        val newActive = if (wasActive) {
            remaining.firstOrNull { p ->
                runCatching { ctx.getDatabasePath(dbNameFor(ctx, p.id)) }.getOrNull()?.exists() == true
            }?.id ?: DEFAULT
        } else activeId(ctx)
        ProfileRegistry.write(ctx, remaining, newActive, ask)
        if (wasActive) ProPrefs.put(ctx, ACTIVE_KEY, newActive)
        // BATCH-5A: flush the active store BEFORE closing — the in-flight save
        // coroutines must finish so the deleted profile's pending writes land
        // before the handle is dropped.
        if (wasActive) {
            try { ProState.flush() } catch (_: Exception) {}
        }
        // P4D-CRASH-04: close the DB handle BEFORE unlinking the file.
        try {
            val dbName = dbNameFor(ctx, profileId)
            ProDb.close(dbName)
            val dbFile = ctx.getDatabasePath(dbName)
            dbFile.delete()
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()
        } catch (_: Exception) {}
        // BATCH-5A: drop the cached SharedPrefs instance for the deleted profile.
        ProfileState.invalidate(profileId)
        // BATCH-5A: reopen + loadAll + theme reload for the new active.
        if (wasActive) {
            ProState.reopenStore(ctx)
            kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) { ProState.loadAll() }
            com.peakform.fitness.ui.ThemeController.reload(ctx)
            ProfileState.stampFirstLaunch(ctx)
            ProState.notifyChanged()
        }
        ProLog.i("PROFILE", "deleted $profileId")
    }

    fun setActive(ctx: Context, profileId: String) {
        ProPrefs.put(ctx, ACTIVE_KEY, profileId)
        val (_, _, ask) = ProfileRegistry.read(ctx)
        val (profiles, _) = ProfileRegistry.read(ctx).let { it.first to it.second }
        ProfileRegistry.write(ctx, profiles, profileId, ask)
    }

    /**
     * Live switch: returns the display name for the toast.
     * BATCH-5A: flush → reopen → loadAll → ThemeController.reload →
     * resolveDark → stampFirstLaunch → notifyChanged, all on Dispatchers.IO
     * via runBlocking so the switch is atomic from the caller's POV.
     * BATCH-4B item 3: after the switch, callers should land on Dashboard.
     */
    fun switchTo(ctx: Context, profileId: String): String {
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            ProState.flush()
            setActive(ctx, profileId)
            // BATCH-5A: drop the cached SharedPrefs instance for the OLD profile
            // so the new profile's prefs are loaded fresh on the next access.
            ProfileState.invalidate(profileId)
            ProState.reopenStore(ctx)
            ProState.loadAll()
            // BATCH-6: reload theme (mode + accent + dynamic) + resolve system dark.
            com.peakform.fitness.ui.ThemeController.reload(ctx)
            com.peakform.fitness.ui.ThemeController.resolveDark(ctx)
            // BATCH-4B item 6: stamp the (possibly new) profile's first-launch
            // timestamp as part of the multi-signal new-user detection.
            ProfileState.stampFirstLaunch(ctx)
            ProState.notifyChanged()
        }
        val p = list(ctx).firstOrNull { it.id == profileId }
        return p?.name ?: profileId
    }

    fun randomAnimalEmoji(ctx: Context): String = try {
        val raw = ctx.assets.open("data/animals.json").bufferedReader().use { it.readText() }
        val arr = ProJson.json.parseToJsonElement(raw)
        val list = (arr as? kotlinx.serialization.json.JsonArray)
            ?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            ?: animalsFallback()
        // animals.json may hold names ("Fox") or emoji — map names to emoji deterministically
        val pick = list.random()
        if (pick.length <= 4 && pick.first().code > 0x2000) pick else animalEmojiFor(pick)
    } catch (_: Exception) {
        animalsFallback().random()
    }

    fun animalEmojiFor(nameOrSeed: String): String {
        val animals = animalsFallback()
        val idx = kotlin.math.abs(nameOrSeed.hashCode()) % animals.size
        return animals[idx]
    }

    /** Legacy P4.Emoji animal set (PF L58495–58507 sample). */
    fun animalsFallback(): List<String> = listOf(
        "🦊", "🐼", "🐨", "🦁", "🐯", "🐻", "🐷", "🐵", "🦍", "🐺",
        "🦉", "🦅", "🦆", "🦢", "🦜", "🐦", "🐧", "🐸", "🐢", "🦎",
        "🐍", "🐉", "🦕", "🦖", "🐳", "🐬", "🦈", "🐙", "🦀", "🦞",
        "🦐", "🐠", "🐟", "🐡", "🦄", "🐴", "🦓", "🦌", "🐮", "🐗",
        "🐏", "🐑", "🐐", "🦙", "🐛", "🦋", "🐌", "🐞", "🐜", "🦗",
        "🕷️", "🦂", "🦟", "🦔", "🦇", "🐻‍❄️", "🦥", "🦦", "🦨", "🦡",
        "🐿️", "🐇", "🐁", "🐀", "🐿", "🐾", "🐘", "🦏", "🦛", "🐪",
    )

    /** Ask-on-boot picker when no active profile decision exists (L60419–60431). */
    fun shouldAskOnBoot(ctx: Context): Boolean {
        val (_, active, ask) = ProfileRegistry.read(ctx)
        val profiles = list(ctx)
        return ask && profiles.size > 1 && (active.isNullOrBlank() || active == DEFAULT && profiles.none { it.id == DEFAULT })
    }
}
