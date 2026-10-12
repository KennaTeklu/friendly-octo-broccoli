package com.peakform.fitness.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState

/**
 * Accessibility — BATCH-13 semantic helpers for screen-reader-friendly UI.
 *
 * Compose already exposes most of the standard semantics; this file collects
 * the patterns used across the app so call sites stay short and consistent.
 *
 * Modifier.accessibleHeading(label) marks a Text as a section heading so
 * TalkBack announces "Heading, <label>".
 *
 * Modifier.accessibleToggle(label, checked) marks a toggle row so TalkBack
 * announces the label + state + role.
 *
 * Modifier.accessibleAction(label) wraps a clickable surface with a clear
 * screen-reader label so non-text buttons (icon buttons, chips) announce
 * their purpose.
 */
object Accessibility {
    /** Section heading semantics (BATCH-13). */
    fun Modifier.accessibleHeading(label: String): Modifier =
        this.semantics(mergeDescendants = true) {
            contentDescription = label
            heading()
        }

    /** Toggleable semantics for toggle rows (BATCH-13). */
    fun Modifier.accessibleToggle(label: String, checked: Boolean): Modifier =
        this.semantics(mergeDescendants = true) {
            contentDescription = label
            role = Role.Switch
            toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            selected = checked
        }

    /** Generic actionable element (BATCH-13) — used by icon-only buttons. */
    fun Modifier.accessibleAction(label: String): Modifier =
        this.semantics(mergeDescendants = true) {
            contentDescription = label
            role = Role.Button
        }
}
