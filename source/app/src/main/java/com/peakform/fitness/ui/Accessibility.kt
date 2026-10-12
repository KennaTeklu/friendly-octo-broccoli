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
 * Accessibility — BATCH-18.
 *
 * Semantic modifier helpers so screens can label interactive elements without
 * repeating boilerplate. Each helper returns a Modifier that attaches the
 * right semantics properties so TalkBack announces the element correctly.
 *
 * Convention: every clickable Box/Row that doesn't already have a Text child
 * with a meaningful label should be wrapped with one of these.
 */
object Accessibility {
    /** Mark an element as a heading (TalkBack announces "Heading"). */
    fun heading(text: String): Modifier = Modifier.semantics {
        contentDescription = text
        heading()
    }

    /** Mark an element as a button with a label. */
    fun button(label: String): Modifier = Modifier.semantics {
        contentDescription = label
        role = Role.Button
    }

    /** Mark an element as a toggle with a label and current state. */
    fun toggle(label: String, on: Boolean): Modifier = Modifier.semantics {
        contentDescription = label
        role = Role.Switch
        toggleableState = if (on) ToggleableState.On else ToggleableState.Off
        selected = on
    }

    /** Mark an element as a selectable option (radio-button semantics). */
    fun option(label: String, selected: Boolean): Modifier = Modifier.semantics {
        contentDescription = label
        role = Role.RadioButton
        this.selected = selected
    }

    /** Mark an element as a slider with a label + current value. */
    fun slider(label: String, value: Float, min: Float, max: Float): Modifier = Modifier.semantics {
        contentDescription = "$label: ${((value - min) / (max - min) * 100).toInt()} percent"
    }
}
