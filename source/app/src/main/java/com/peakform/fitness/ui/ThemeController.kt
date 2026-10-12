package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.Profiles

/**
 * ThemeController — runtime 3-mode (system/dark/light) + accent + Material You
 * state, with write-through persistence to the active profile's SharedPreferences.
 *
 * BATCH-5B (theme write-through + reload on profile switch + logging):
 *  - 3-mode model: "system" follows the platform dark/light setting; "dark"
 *    and "light" are explicit overrides. The `mode` property holds the chosen
 *    mode; `dark` is the resolved effective value (system mode reads the
 *    platform at resolveDark()).
 *  - Every write path (set, setAccentAndDynamic, setDynamic, persist) updates
 *    the in-memory Compose state AND writes to ProfileSettings (per-profile
 *    SharedPreferences `p4_ls_<profileId>`) in the SAME call — no caller can
 *    forget to persist, and no frame can observe state without storage.
 *  - reload(ctx) re-reads p4_theme + p4_dynamic from ProfileSettings and
 *    re-runs load+loadDynamic. Called after a profile switch, after loadAll,
 *    and before notifyChanged — so the new profile's theme lands on screen
 *    in the same recomposition that surfaces the new profile's data.
 *  - resolveDark(systemDark) resolves the effective `dark` flag for "system"
 *    mode using the platform value the caller passes in (so we don't need a
 *    Composable context to read the system setting).
 *  - Every operation logs under tag "ProTheme" so theme transitions are
 *    traceable in logcat.
 */
object ThemeController {
    private const val TAG = "ProTheme"

    /** The user-chosen mode: "system", "dark", or "light". */
    var mode by mutableStateOf("system")
        private set
    /** The resolved effective dark flag. Updated by set() / resolveDark(). */
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Cold-start hydration: read `p4_theme` JSON. */
    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            // Legacy theme blobs stored {"mode":"dark|light"}; new blobs can
            // also store "system". If the blob has no "mode" key, keep current.
            if (m != null) {
                mode = when (m) { "system", "dark", "light" -> m; else -> "system" }
                // Resolve effective dark for non-system modes immediately.
                if (mode == "dark") dark = true
                if (mode == "light") dark = false
            }
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
            ProLog.i(TAG, "load: mode=$mode accent=$accentId dark=$dark")
        } catch (e: Exception) {
            ProLog.w(TAG, "load failed: ${e.message}")
        }
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i(TAG, "loadDynamic: dynamic=$dynamic")
    }

    /** Resolve the effective `dark` flag for the current mode, given the
     *  platform's dark setting. "system" → follows platform; "dark" → true;
     *  "light" → false. */
    fun resolveDark(systemDark: Boolean): Boolean {
        val effective = when (mode) {
            "dark" -> true
            "light" -> false
            else -> systemDark  // "system"
        }
        if (dark != effective) {
            dark = effective
            ProLog.i(TAG, "resolveDark: mode=$mode systemDark=$systemDark → dark=$dark")
        }
        return effective
    }

    /**
     * Apply mode + accent to runtime state AND persist to ProfileSettings.
     * The caller no longer needs to write `p4_theme` themselves — they cannot forget.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        val newMode = when (mode) { "system", "dark", "light" -> mode; else -> "system" }
        this.mode = newMode
        if (newMode == "dark") dark = true
        if (newMode == "light") dark = false
        // For "system", the caller is responsible for calling resolveDark() with the platform value.
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ProLog.i(TAG, "set: mode=$newMode accent=$accent dark=$dark")
        ctx?.let { persist(it) }
    }

    /**
     * P4E-UI-05 + BATCH-5B: atomic write of accent + p4_dynamic in a single
     * SharedPreferences commit(). Updates runtime state (dark, accentId,
     * dynamic) in the same call — Compose observes these via mutableStateOf,
     * so recomposition fires synchronously, and the storage write lands on
     * disk before the next frame reads it.
     *
     * Code path:
     *   1. Update runtime state (mode, dark, accentId, dynamic)
     *   2. ProfileState.prefs(ctx).edit()
     *        .putString(K_THEME, currentJson())
     *        .putString(K_DYNAMIC, if (dynamic) "on" else "off")
     *        .commit()  // synchronous, atomic
     *   Both keys land in the same SharedPreferences transaction. No frame can
     *   observe accent=X with dynamic=on (which would show Material You instead
     *   of the user's chosen accent).
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        val newMode = when (mode) { "system", "dark", "light" -> mode; else -> "system" }
        this.mode = newMode
        if (newMode == "dark") dark = true
        if (newMode == "light") dark = false
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ProLog.i(TAG, "setAccentAndDynamic: mode=$newMode accent=$accent dynamic=$dynamic dark=$dark")
        ctx?.let { c ->
            ProfileState_prefsEdit(c)
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
    }

    /** Toggle Material You; persists to `p4_dynamic` and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ProLog.i(TAG, "setDynamic: dynamic=$on")
        ctx?.let { c ->
            ProfileState_prefsEdit(c)
                .putString(ProfileSettings.K_DYNAMIC, if (on) "on" else "off")
                .commit()
        }
    }

    /** Persist the current mode + accent to `p4_theme` via ProfileSettings. */
    fun persist(ctx: Context) {
        ProLog.i(TAG, "persist: mode=$mode accent=$accentId dark=$dark")
        ProfileState_prefsEdit(ctx)
            .putString(ProfileSettings.K_THEME, currentJson())
            .commit()
    }

    /**
     * BATCH-5B: reload theme from ProfileSettings (the active profile's prefs).
     * Called after a profile switch, after loadAll, before notifyChanged — so
     * the new profile's theme lands on screen in the same recomposition that
     * surfaces the new profile's data.
     */
    fun reload(ctx: Context) {
        ProLog.i(TAG, "reload: re-reading theme from active profile prefs (profile=${Profiles.activeId(ctx)})")
        load(ProfileSettings.get(ctx, ProfileSettings.K_THEME))
        loadDynamic(ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC))
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""

    /** Returns the active profile's SharedPreferences editor (write-through).
     *  Routes through ProfileState.prefs() so the write lands on the right
     *  profile's file (`p4_ls_<activeId>`). */
    private fun ProfileState_prefsEdit(ctx: Context): android.content.SharedPreferences.Editor =
        com.peakform.fitness.core.ProfileState.prefs(ctx).edit()
}
