package com.peakform.fitness.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.peakform.fitness.ProLog
import android.content.Intent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * LibraryCustom — Library Studio persistence (legacy p4_custom_ex + studio merge reports).
 * Custom exercises are merged into the live library by engine/Library.ensure.
 */
object LibraryCustom {
    private val AVATARS_LIST: List<String> = listOf(
        "🐶","🐱","🐭","🐹","🐰","🦊","🐻","🐼","🐨","🐯","🦁","🐮","🐷","🐸","🐵","🐔",
        "🐧","🐦","🐤","🦆","🦅","🦉","🦇","🐺","🐗","🐴","🦄","🐝","🐛","🦋","🐌","🐞",
        "🐜","🦗","🕷️","🦂","🐢","🐍","🦎","🐙","🦑","🦐","🦀","🐡","🐠","🐟","🐬","🐳",
        "🐋","🦈","🐊","🐆","🦓","🦍","🐘","🦏","🦛","🐪","🦒","🐃","🐂","🐄","🐎","🐐",
    )
    val AVATARS: List<String> get() = AVATARS_LIST

    private fun file(ctx: Context): File = File(ctx.filesDir, "studio/custom_exercises.json")

    fun list(ctx: Context): List<LibraryExercise> = try {
        val f = file(ctx)
        if (!f.exists()) emptyList()
        else Json.parseToJsonElement(f.readText()).jsonArray.map { el ->
            ProJson.decode(LibraryExercise.serializer(), el.toString())
        }
    } catch (_: Exception) { emptyList() }

    fun add(ctx: Context, ex: LibraryExercise) {
        val cur = list(ctx).filter { it.id != ex.id } + ex
        persist(ctx, cur)
    }

    fun remove(ctx: Context, id: String) {
        persist(ctx, list(ctx).filter { it.id != id })
    }

    private fun persist(ctx: Context, list: List<LibraryExercise>) {
        try {
            val f = file(ctx)
            f.parentFile?.mkdirs()
            f.writeText(ProJson.pretty.encodeToString(
                JsonArray.serializer(),
                JsonArray(list.map { ProJson.encodeElement(LibraryExercise.serializer(), it) })))
        } catch (e: Exception) {
            ProLog.e("STUDIO", "persist failed: ${e.message}")
        }
    }

    /** Legacy exercise-library export shape: verbatim LibraryExercise records + meta. */
    fun exportLibrary(ctx: Context): String {
        val custom = list(ctx)
        val payload = JsonObject(mapOf(
            "kind" to JsonPrimitive("Pro exercise library export"),
            "_exportedAt" to JsonPrimitive(ProState.nowIso()),
            "appVersion" to JsonPrimitive("4.0.0-native-1.3.0"),
            "exercises" to JsonArray(custom.map { ProJson.encodeElement(LibraryExercise.serializer(), it) }),
        ))
        return ProJson.pretty.encodeToString(JsonObject.serializer(), payload)
    }

    /**
     * Import with merge report: matching ids skipped, new ids added.
     * Returns (added, skipped, failed).
     */
    fun importLibrary(ctx: Context, raw: String): Triple<Int, Int, Int> {
        var added = 0; var skipped = 0; var failed = 0
        try {
            val healed = Heal.heal(raw)
            val obj = Json.parseToJsonElement(healed)
            val arr: JsonArray = when {
                obj is JsonArray -> obj
                obj is JsonObject && obj.containsKey("exercises") -> obj["exercises"]!!.jsonArray
                else -> return Triple(0, 0, 1)
            }
            val cur = list(ctx).toMutableList()
            for (el in arr) {
                try {
                    val ex = ProJson.decode(LibraryExercise.serializer(), el.toString())
                    if (cur.any { it.id == ex.id }) skipped++ else { cur.add(ex); added++ }
                } catch (_: Exception) { failed++ }
            }
            persist(ctx, cur)
        } catch (_: Exception) {
            return Triple(added, skipped, failed + 1)
        }
        return Triple(added, skipped, failed)
    }
}

/**
 * ProfileSwitcher — per-profile data namespaces (legacy p4_profile_data_<id>).
 * Switch: namespace-save current data → namespace-load target data → notify.
 */
object ProfileSwitcher {
    private fun nsFile(ctx: Context, id: String): File = File(File(ctx.filesDir, "profiles").apply { mkdirs() }, "$id.json")

    /** Save ProState.data + draft into the given profile's namespace file. */
    fun saveNamespace(ctx: Context, id: String) {
        try {
            val payload = JsonObject(mapOf(
                "workoutData" to ProState.data.toJsonElement(),
                "currentWorkout" to (ProState.currentWorkout?.let {
                    ProJson.encodeElement(WorkoutRecord.serializer(), it)
                } ?: kotlinx.serialization.json.JsonNull),
            ))
            nsFile(ctx, id).writeText(ProJson.json.encodeToString(JsonObject.serializer(), payload))
        } catch (e: Exception) {
            ProLog.e("PROFILES", "namespace save failed: ${e.message}")
        }
    }

    /** Load a namespace into ProState (blank → fresh data). Returns a human message. */
    fun loadNamespace(ctx: Context, id: String): String {
        val f = nsFile(ctx, id)
        return try {
            if (f.exists()) {
                val payload = Json.parseToJsonElement(f.readText()).jsonObject
                val data = ProJson.decodeElement(
                    WorkoutData.serializer(), payload["workoutData"]!!.jsonObject)
                ProState.data = data
                ProState.currentWorkout = payload["currentWorkout"]?.let {
                    if (it is kotlinx.serialization.json.JsonNull) null
                    else ProJson.decodeElement(WorkoutRecord.serializer(), it.jsonObject)
                }
                "Profile loaded — ${data.workouts.size} workouts"
            } else {
                ProState.data = WorkoutData()
                ProState.currentWorkout = null
                "Fresh profile — first workout will set it up"
            }
        } catch (e: Exception) {
            ProLog.e("PROFILES", "namespace load failed: ${e.message}")
            "Profile loaded (some data could not be read)"
        }
    }

    /** Full switch: save current → set active → load target. */
    fun switch(ctx: Context, targetId: String): String {
        val (profiles, active, ask) = ProfileRegistry.read(ctx)
        if (active != null) saveNamespace(ctx, active)
        val msg = loadNamespace(ctx, targetId)
        ProfileRegistry.write(ctx, profiles, targetId, ask)
        ProState.flush()
        return msg
    }

    fun delete(ctx: Context, id: String) {
        val (profiles, active, ask) = ProfileRegistry.read(ctx)
        ProfileRegistry.write(ctx, profiles.filter { it.id != id }, active?.takeIf { it != id } ?: active, ask)
        nsFile(ctx, id).delete()
    }

    /** On startup: hydrate the active profile's namespace (called after loadAll). */
    fun hydrateActive(ctx: Context) {
        val active = ProfileRegistry.read(ctx).second ?: return
        val legacyActive = ProPrefs.get(ctx, "p4_active_profile")
        val id = active.ifBlank { legacyActive ?: return }
        // Only hydrate from namespace when the Room store is empty (fresh device state)
        if (ProState.data.workouts.isEmpty() && ProState.data.exercises.isEmpty()) {
            loadNamespace(ctx, id)
        }
    }
}

/** Clipboard + share helpers. */
object ClipboardShare {
    fun copy(ctx: Context, text: String, label: String = "Pro"): Boolean = try {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
        true
    } catch (_: Exception) { false }

    fun systemShare(ctx: Context, text: String, subject: String = "My Pro workout") {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        ctx.startActivity(Intent.createChooser(send, "Share via").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
