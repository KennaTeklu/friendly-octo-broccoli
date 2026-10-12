package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

object ProfileState {
    const val K_ONBOARDED = "p4_onboarded"
    const val K_OB_DRAFT = "p4_ob_draft"
    const val K_OB_FINISHED_AT = "p4_ob_finished_at"
    const val K_FIRST_LAUNCH = "p4_first_launch"
    const val K_HEALTH_SCREENED = "p4_health_screened_at"
    const val K_HEALTH_TIER = "p4_health_tier"
    const val K_HEALTH_GENERAL = "p4_health_general"
    const val K_HEALTH_FOLLOWUP = "p4_health_followup"
    const val K_HEALTH_JOINTS = "p4_health_joints"
    const val K_HEALTH_UNLOCKED = "p4_health_unlocked_at"
    const val K_HEALTH_DECLARED = "p4_health_declared"
    const val K_HEALTH_INTRO_SEEN = "p4_health_intro_seen"
    const val K_EMERGENCY_BACKUP = "workoutEmergencyBackup"
    const val K_V18_MIGRATED = "p4_v18_migrated"

    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.getSharedPreferences("p4_ls_$profileId", Context.MODE_PRIVATE)

    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    fun get(ctx: Context, key: String, default: String? = null): String? = prefs(ctx).getString(key, default)
    fun put(ctx: Context, key: String, value: String) { prefs(ctx).edit().putString(key, value).apply() }
    fun putSync(ctx: Context, key: String, value: String) { prefs(ctx).edit().putString(key, value).commit() }
    fun remove(ctx: Context, key: String) { prefs(ctx).edit().remove(key).apply() }
    fun has(ctx: Context, key: String): Boolean = prefs(ctx).contains(key)
    fun keys(ctx: Context): List<String> = prefs(ctx).all.keys.toList()

    fun isNewUser(ctx: Context): Boolean {
        if (get(ctx, K_ONBOARDED) == "true") return false
        if (get(ctx, K_HEALTH_SCREENED) != null) return false
        if (ProState.data.workouts.isNotEmpty()) return false
        if (ProState.data.exercises.isNotEmpty()) return false
        if (get(ctx, K_FIRST_LAUNCH) != null) return false
        return true
    }

    fun stampFirstLaunch(ctx: Context) {
        if (get(ctx, K_FIRST_LAUNCH) == null) put(ctx, K_FIRST_LAUNCH, ProState.nowIso())
    }

    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V18_MIGRATED) == "true") return
        val profiles = Profiles.list(ctx)
        val legacyOnboarded = ProPrefs.get(ctx, K_ONBOARDED)
        val legacyObDraft = ProPrefs.get(ctx, K_OB_DRAFT)
        val legacyObFinishedAt = ProPrefs.get(ctx, K_OB_FINISHED_AT)
        val legacyHealth = mapOf(
            K_HEALTH_SCREENED to ProPrefs.get(ctx, K_HEALTH_SCREENED),
            K_HEALTH_TIER to ProPrefs.get(ctx, K_HEALTH_TIER),
            K_HEALTH_GENERAL to ProPrefs.get(ctx, K_HEALTH_GENERAL),
            K_HEALTH_FOLLOWUP to ProPrefs.get(ctx, K_HEALTH_FOLLOWUP),
            K_HEALTH_JOINTS to ProPrefs.get(ctx, K_HEALTH_JOINTS),
            K_HEALTH_UNLOCKED to ProPrefs.get(ctx, K_HEALTH_UNLOCKED),
            K_HEALTH_DECLARED to ProPrefs.get(ctx, K_HEALTH_DECLARED),
            K_EMERGENCY_BACKUP to ProPrefs.get(ctx, K_EMERGENCY_BACKUP),
        )
        for (p in profiles) {
            val sp = prefsFor(ctx, p.id)
            val ed = sp.edit()
            if (legacyOnboarded != null && !sp.contains(K_ONBOARDED)) ed.putString(K_ONBOARDED, legacyOnboarded)
            if (legacyObDraft != null && !sp.contains(K_OB_DRAFT)) ed.putString(K_OB_DRAFT, legacyObDraft)
            if (legacyObFinishedAt != null && !sp.contains(K_OB_FINISHED_AT)) ed.putString(K_OB_FINISHED_AT, legacyObFinishedAt)
            legacyHealth.forEach { (k, v) -> if (v != null && !sp.contains(k)) ed.putString(k, v) }
            ed.apply()
        }
        ProPrefs.put(ctx, K_V18_MIGRATED, "true")
        ProLog.i("MIGRATE", "v18 meta migration complete for ${profiles.size} profiles")
    }
}
