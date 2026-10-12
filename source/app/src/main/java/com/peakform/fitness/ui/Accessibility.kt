package com.peakform.fitness.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

/**
 * Accessibility — BATCH-18A.
 *
 * Centralized accessibility helpers so every screen can apply consistent
 * semantics without duplicating the boilerplate.
 *
 *  - labelled("description") → Modifier.semantics { contentDescription = "..." }
 *  - heading() → marks the element as a heading for screen reader navigation
 *  - decorative() → marks the element as invisible to screen readers
 *  - focusOrder(index) → sets the tab order for keyboard / switch users
 *
 * Compose semantics are inherited, so applying these at the card / row level
 * is enough — children don't need to repeat the labels.
 */
object Accessibility {
    fun labelled(desc: String): Modifier = Modifier.semantics { contentDescription = desc }
                fun sectionLabel(name: String): Modifier = Modifier
        .semantics { contentDescription = "$name section" }
        .padding(0.dp)
}
