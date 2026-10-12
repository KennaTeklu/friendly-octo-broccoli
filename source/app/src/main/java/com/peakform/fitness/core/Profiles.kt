package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.ui.ThemeController
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.File
import java.util.UUID

/**
 * Profiles — full per-profile data isolation (PF P4.Profiles L58572–58728).
 * Each profile owns a Room namespace: the legacy "pro.db" file stays the
 * default profile's store (additive migration — existing users keep everything),
 * other profiles get pro_<id>.db. Switching is live: flush → reopen store →
 * loadAll → reload theme → notify (no restart).
 *
 * BATCH-5B: switchTo() now also reloads ThemeController from the new profile's
 *   per-profile SharedPreferences so theme switches with the profile.
 * BATCH-11: delete() picks the new active profile only from the set whose Room
 *   DB file exists on disk — switching to a "phantom" profile (registry entry
 *   with no DB) crashed the app on cold start. switchTo() now refuses to switch
 *   to a profile whose DB file is missing, returning the active profile name
 *   unchanged.
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
        // BATCH-11: flush the active store BEFORE closing the DB handle, so any
        // in-flight save coroutines finish writing instead of being orphaned
        // mid-write (P4D-CRASH-01/03 race window).
        if (wasActive) ProState.flush()
        // BATCH-11: pick the new active profile ONLY from profiles whose Room
        // DB file actually exists on disk. Switching to a "phantom" profile
        // (registry entry with no DB) crashed the app on cold start because
        // loadAll() read empty tables and there was no path back.
        val newActive = if (wasActive) {
            remaining.firstOrNull { p -> dbFileExists(ctx, p.id) }?.id ?: DEFAULT
        } else {
            activeId(ctx)
        }
        ProfileRegistry.write(ctx, remaining, newActive, ask)
        if (wasActive) ProPrefs.put(ctx, ACTIVE_KEY, newActive)
        // P4D-CRASH-04: close the DB handle BEFORE unlinking the file.
        // BATCH-11: also flush a second time after close in case the active
        // profile was the deleted one and the new active needs its store
        // opened. reopenStore + loadAll below bring the in-memory state in
        // sync with the new active profile.
        try {
            val dbName = dbNameFor(ctx, profileId)
            ProDb.close(dbName)
            val dbFile = ctx.getDatabasePath(dbName)
            dbFile.delete()
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()
        } catch (_: Exception) {}
        if (wasActive) {
            // reopen the new active profile's store, load its data, reload theme
            // from its per-profile SharedPreferences, and notify listeners.
            ProState.reopenStore(ctx)
            kotlinx.coroutines.runBlocking(Dispatchers.IO) { ProState.loadAll() }
            ThemeController.reload(ctx)
            ProfileState.stampFirstLaunch(ctx)
            ProState.notifyChanged()
        }
        ProLog.i("PROFILE", "deleted $profileId; new active=$newActive")
    }

    /** BATCH-11: returns true if the profile's Room DB file exists on disk. */
    fun dbFileExists(ctx: Context, profileId: String): Boolean = try {
        val dbName = dbNameFor(ctx, profileId)
        ctx.getDatabasePath(dbName).exists()
    } catch (_: Exception) { false }

    fun setActive(ctx: Context, profileId: String) {
        ProPrefs.put(ctx, ACTIVE_KEY, profileId)
        val (_, _, ask) = ProfileRegistry.read(ctx)
        val (profiles, _) = ProfileRegistry.read(ctx).let { it.first to it.second }
        ProfileRegistry.write(ctx, profiles, profileId, ask)
    }

    /**
     * Live switch: returns the display name for the toast.
     * BATCH-5B: reloads ThemeController from the new profile's per-profile
     *   SharedPreferences so a theme change in profile A does not bleed into
     *   profile B (write-through + reload contract).
     * BATCH-11: refuses to switch to a profile whose Room DB file is missing —
     *   returns the current profile name unchanged and logs the refusal so the
     *   user is never stranded on a profile they cannot open.
     */
    fun switchTo(ctx: Context, profileId: String): String {
        if (profileId == activeId(ctx)) return list(ctx).firstOrNull { it.id == profileId }?.name ?: profileId
        if (!dbFileExists(ctx, profileId)) {
            ProLog.w("PROFILE", "switchTo refused — $profileId DB missing; staying on ${activeId(ctx)}")
            return list(ctx).firstOrNull { it.id == activeId(ctx) }?.name ?: activeId(ctx)
        }
        kotlinx.coroutines.runBlocking(Dispatchers.IO) {
            ProState.flush()
            setActive(ctx, profileId)
            ProState.reopenStore(ctx)
            ProState.loadAll()
        }
        // BATCH-5B: reload the theme from the new active profile's SharedPreferences.
        ThemeController.reload(ctx)
        // BATCH-4B item 6: stamp the (possibly new) profile's first-launch
        // timestamp as part of the multi-signal new-user detection.
        ProfileState.stampFirstLaunch(ctx)
        ProState.notifyChanged()
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
