package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.File
import java.util.UUID

/**
 * Profiles — full per-profile data isolation (PF P4.Profiles L58572–58728).
 * Each profile owns a Room namespace: the legacy "pro.db" file stays the default profile's
 * store (additive migration — existing users keep everything), other profiles get
 * pro_<id>.db. Switching is live: flush → reopen store → loadAll → notify (no restart).
 */
object Profiles {

    const val DEFAULT = "default"
    const val ACTIVE_KEY = "p4_active_profile"

    data class ProfileMeta(val id: String, val name: String, val emoji: String?)

    fun activeId(ctx: Context): String =
        ProPrefs.get(ctx, ACTIVE_KEY)?.takeIf { it.isNotBlank() } ?: DEFAULT

    fun dbNameFor(ctx: Context, profileId: String): String =
        if (profileId == DEFAULT) "pro.db" else "pro_$profileId.db"

    fun list(ctx: Context): List<ProfileMeta> {
        val (profiles, _, _) = ProfileRegistry.read(ctx)
        val base = profiles.map { ProfileMeta(it.id, it.name, it.emoji) }
        return if (base.isEmpty()) listOf(ProfileMeta(DEFAULT, "Default", "🏋️")) else base
    }

    fun create(ctx: Context, name: String, emoji: String? = null): ProfileMeta {
        val id = "p_${UUID.randomUUID().toString().take(8)}"
        val emojiFinal = emoji ?: randomAnimalEmoji(ctx)
        val (profiles, _, ask) = ProfileRegistry.read(ctx)
        val newProfile = ProfileRegistry.Profile(id, name, emojiFinal, System.currentTimeMillis(), profiles.size)
        ProfileRegistry.write(ctx, profiles + newProfile, activeId(ctx), ask)
        ProLog.i("PROFILE", "created $id ($name)")
        return ProfileMeta(id, name, emojiFinal)
    }

    fun delete(ctx: Context, profileId: String) {
        if (profileId == DEFAULT) return // the default namespace is never deletable
        val (profiles, _, ask) = ProfileRegistry.read(ctx)
        val remaining = profiles.filter { it.id != profileId }
        val wasActive = activeId(ctx) == profileId
        ProfileRegistry.write(ctx, remaining, if (wasActive) (remaining.firstOrNull()?.id ?: DEFAULT) else activeId(ctx), ask)
        // delete the profile's database file
        try {
            val dbFile = ctx.getDatabasePath(dbNameFor(ctx, profileId))
            dbFile.delete()
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()
        } catch (_: Exception) {}
        ProLog.i("PROFILE", "deleted $profileId")
    }

    fun setActive(ctx: Context, profileId: String) {
        ProPrefs.put(ctx, ACTIVE_KEY, profileId)
        val (_, _, ask) = ProfileRegistry.read(ctx)
        val (profiles, _) = ProfileRegistry.read(ctx).let { it.first to it.second }
        ProfileRegistry.write(ctx, profiles, profileId, ask)
    }

    /** Live switch: returns the display name for the toast. */
    fun switchTo(ctx: Context, profileId: String): String {
        ProState.flush()
        setActive(ctx, profileId)
        ProState.reopenStore(ctx)
        kotlinx.coroutines.runBlocking { ProState.loadAll() }
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
