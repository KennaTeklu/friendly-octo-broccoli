package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ui.ThemeController
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * OnboardDraft — resumable wizard draft (legacy p4_ob_draft, PF L56524–56531)
 * plus the finish() side effects (L56533–56573): profile write, theme, vocab,
 * consent records, history rescue, confetti trigger flag.
 */
data class OnboardDraft(
    val step: Int = 0,
    val name: String = "",
    val birth: String = "",
    val gender: String = "unspecified",
) {
    companion object {
        const val KEY = "p4_ob_draft"

        fun fromPrefs(ctx: Context): OnboardDraft = try {
            val raw = ProPrefs.get(ctx, KEY) ?: return OnboardDraft()
            val o = ProJson.json.parseToJsonElement(raw).jsonObject
            OnboardDraft(
                step = (o["step"] as? JsonPrimitive)?.contentOrNull?.toIntOrNull()?.coerceIn(0, 4) ?: 0,
                name = (o["name"] as? JsonPrimitive)?.contentOrNull ?: "",
                birth = (o["birth"] as? JsonPrimitive)?.contentOrNull ?: "",
                gender = (o["gender"] as? JsonPrimitive)?.contentOrNull ?: "unspecified",
            )
        } catch (_: Exception) { OnboardDraft() }

        /** History rescue: wizard is redundant for users who already logged workouts (L56514–56521). */
        fun needed(ctx: Context): Boolean {
            if (ProPrefs.get(ctx, "p4_onboarded") == "true") return false
            return true
        }

        fun finish(
            ctx: Context,
            name: String,
            birth: String?,
            gender: String,
            level: Int,
            mode: String,
            accentId: String,
        ) {
            // profile write
            val d = ProState.data
            ProState.data = d.copy(user = d.user.copy(
                name = name,
                birthDate = birth?.takeIf { it.isNotBlank() },
                gender = gender,
            ))
            ProState.saveWorkoutData()
            // theme + vocab
            ThemeController.set(mode, accentId)
            ProPrefs.put(ctx, "p4_theme", """{"mode":"$mode","accent":"$accentId"}""")
            Vocab.setLevel(ctx, level)
            // consents recorded in one action (L56559–56563)
            Consent.record(ctx, "terms", "Onboarding completed — Terms accepted")
            Consent.record(ctx, "privacy", "Onboarding completed — Privacy Policy accepted")
            Consent.record(ctx, "waiver", "Onboarding completed — Liability Waiver accepted")
            ProPrefs.put(ctx, "p4_onboarded", "true")
            ProPrefs.remove(ctx, KEY)
            ProPrefs.put(ctx, "p4_ob_finished_at", ProState.nowIso())
            ProState.notifyChanged()
        }
    }
}
