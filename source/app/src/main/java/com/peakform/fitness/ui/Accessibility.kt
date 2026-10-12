package com.peakform.fitness.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.clearAndSetSemantics

/**
 * Accessibility — small helpers that wrap the most common a11y patterns.
 *
 * BATCH-13: every interactive element should be reachable by TalkBack and
 * announce its role + label. These helpers keep the call sites readable.
 *
 * NOTE: We do NOT use invisibleToUser(), heading(), or traversalIndex() —
 * these are experimental APIs in Compose 1.7+ that require @OptIn and have
 * known stability issues. We stick to the stable contentDescription property
 * and the role semantics, which are sufficient for TalkBack to navigate
 * the app.
 */
object Accessibility {

    /** Apply a contentDescription to a node — TalkBack announces this when the
     *  element receives focus. Use on icons, images, and any control without
     *  a visible label. */
    fun Modifier.describe(label: String): Modifier =
        this.semantics { contentDescription = label }

    /** Apply a role to a node — TalkBack announces "button" / "checkbox" /
     *  "switch" etc. after the label. */
    fun Modifier.withRole(role: Role): Modifier =
        this.semantics { this.role = role }

    /** Apply both a label and a role in one call — the most common combo. */
    fun Modifier.labelled(label: String, role: Role? = null): Modifier =
        this.semantics {
            contentDescription = label
            if (role != null) this.role = role
        }

    /** Hide a node entirely from TalkBack (decorative images, dividers).
     *  Use sparingly — most nodes SHOULD be reachable. */
    fun Modifier.hiddenFromA11y(): Modifier =
        this.clearAndSetSemantics { }

    /** Min touch-target size (48dp) — Android accessibility guideline. */
    val MinTouchTarget = 48
}
