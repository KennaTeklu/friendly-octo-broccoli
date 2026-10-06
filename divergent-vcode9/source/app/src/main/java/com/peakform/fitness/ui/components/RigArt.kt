package com.peakform.fitness.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peakform.fitness.core.LibraryExercise
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * RigArt — v1.4.0 "exercise media" engine (review board: UI expert techniques 1/2/7/8/11).
 *
 * The legacy library was text-only (HTML expert: no <img>/<video>/emoji per exercise — the
 * only imagery was a live Wikipedia thumbnail lookup). This is the native above-and-beyond:
 * every one of the 1,413 exercises gets a procedurally drawn, looping animated pictogram —
 * an 11-joint side-view stick athlete that actually PERFORMS the movement pattern — on a
 * muscle-themed breathing glow. Zero bytes of shipped imagery; pure Canvas, offline, tiny.
 *
 * Performance (systems expert REC 6/7):
 *  - the Library screen provides ONE shared clock via LocalRigClock; reading `.value` inside
 *    the draw phase invalidates drawing only — no recomposition per frame.
 *  - when no clock is provided (detail sheet, tests), RigArt owns a local one; LazyColumn
 *    removes off-screen cards from composition entirely.
 *  - `playing = false` freezes the pose without ever reading a clock state (zero frames).
 */

/** Provides the shared animation clock (0f..1f loop). LibraryScreen hosts it. */
val LocalRigClock = compositionLocalOf<State<Float>?> { null }

@Composable
fun rememberRigClock(): State<Float> {
    val t = rememberInfiniteTransition("rig-clock")
    return t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "rig-t",
    )
}

/** Pose = 7 joints × (x,y) in 0..1 unit space: head, neck, elbow, wrist, hip, knee, ankle. */
private fun P(vararg xy: Float): FloatArray = xy

private val STAND = P(0.50f, 0.115f, 0.50f, 0.225f, 0.465f, 0.360f, 0.435f, 0.470f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f)

private fun shift(p: FloatArray, dy: Float, dx: Float = 0f): FloatArray =
    FloatArray(p.size) { i -> if (i % 2 == 0) p[i] + dx else p[i] + dy }

/** RigDef — keyframes + prop flags. bar: 0 none · 1 at hands · 2 at shoulders. */
data class RigDef(val keys: List<FloatArray>, val bar: Int = 0, val bench: Boolean = false, val topBar: Boolean = false)

val RIG_PATTERNS: Map<String, RigDef> = mapOf(
    "stand" to RigDef(listOf(STAND, shift(STAND, -0.010f))),
    "squat" to RigDef(
        listOf(
            STAND,
            P(0.44f, 0.215f, 0.45f, 0.320f, 0.40f, 0.40f, 0.56f, 0.44f, 0.46f, 0.615f, 0.615f, 0.670f, 0.560f, 0.905f),
        ),
        bar = 2,
    ),
    "hinge" to RigDef(
        listOf(
            STAND,
            P(0.28f, 0.300f, 0.31f, 0.385f, 0.33f, 0.505f, 0.345f, 0.615f, 0.46f, 0.530f, 0.555f, 0.700f, 0.565f, 0.915f),
        ),
        bar = 1,
    ),
    "bench" to RigDef(
        listOf(
            P(0.16f, 0.560f, 0.25f, 0.585f, 0.30f, 0.500f, 0.305f, 0.360f, 0.47f, 0.600f, 0.615f, 0.730f, 0.740f, 0.870f),
            P(0.16f, 0.560f, 0.25f, 0.585f, 0.31f, 0.620f, 0.305f, 0.500f, 0.47f, 0.600f, 0.615f, 0.730f, 0.740f, 0.870f),
        ),
        bar = 1,
        bench = true,
    ),
    "press" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.395f, 0.395f, 0.430f, 0.275f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.105f, 0.51f, 0.215f, 0.575f, 0.150f, 0.545f, 0.060f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
        bar = 1,
    ),
    "pushup" to RigDef(
        listOf(
            P(0.16f, 0.520f, 0.25f, 0.545f, 0.27f, 0.660f, 0.285f, 0.860f, 0.49f, 0.590f, 0.665f, 0.730f, 0.810f, 0.865f),
            P(0.15f, 0.610f, 0.24f, 0.635f, 0.27f, 0.710f, 0.285f, 0.860f, 0.48f, 0.665f, 0.665f, 0.775f, 0.815f, 0.880f),
        ),
    ),
    "pullup" to RigDef(
        listOf(
            P(0.50f, 0.400f, 0.50f, 0.480f, 0.475f, 0.250f, 0.500f, 0.120f, 0.52f, 0.650f, 0.565f, 0.790f, 0.545f, 0.870f),
            P(0.50f, 0.220f, 0.50f, 0.310f, 0.445f, 0.340f, 0.500f, 0.120f, 0.52f, 0.520f, 0.585f, 0.680f, 0.550f, 0.780f),
        ),
        topBar = true,
    ),
    "row" to RigDef(
        listOf(
            P(0.26f, 0.300f, 0.29f, 0.370f, 0.325f, 0.520f, 0.330f, 0.660f, 0.46f, 0.530f, 0.555f, 0.700f, 0.565f, 0.915f),
            P(0.26f, 0.300f, 0.29f, 0.370f, 0.400f, 0.500f, 0.415f, 0.430f, 0.46f, 0.530f, 0.555f, 0.700f, 0.565f, 0.915f),
        ),
    ),
    "curl" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.455f, 0.360f, 0.440f, 0.470f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.440f, 0.365f, 0.360f, 0.250f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "pushdown" to RigDef(
        listOf(
            P(0.50f, 0.125f, 0.50f, 0.235f, 0.435f, 0.330f, 0.415f, 0.400f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.460f, 0.360f, 0.475f, 0.520f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "raise" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.460f, 0.360f, 0.440f, 0.470f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.105f, 0.50f, 0.220f, 0.385f, 0.310f, 0.290f, 0.235f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "fly" to RigDef(
        listOf(
            P(0.16f, 0.560f, 0.25f, 0.585f, 0.36f, 0.470f, 0.385f, 0.330f, 0.47f, 0.600f, 0.615f, 0.730f, 0.740f, 0.870f),
            P(0.16f, 0.560f, 0.25f, 0.585f, 0.325f, 0.560f, 0.300f, 0.640f, 0.47f, 0.600f, 0.615f, 0.730f, 0.740f, 0.870f),
        ),
        bench = true,
    ),
    "crunch" to RigDef(
        listOf(
            P(0.17f, 0.640f, 0.24f, 0.625f, 0.28f, 0.540f, 0.30f, 0.470f, 0.47f, 0.620f, 0.615f, 0.500f, 0.640f, 0.700f),
            P(0.23f, 0.585f, 0.29f, 0.575f, 0.32f, 0.500f, 0.33f, 0.440f, 0.47f, 0.620f, 0.615f, 0.500f, 0.640f, 0.700f),
        ),
    ),
    "plank" to RigDef(
        listOf(
            P(0.16f, 0.540f, 0.25f, 0.560f, 0.27f, 0.670f, 0.285f, 0.865f, 0.49f, 0.600f, 0.665f, 0.740f, 0.815f, 0.875f),
            P(0.16f, 0.530f, 0.25f, 0.550f, 0.27f, 0.665f, 0.285f, 0.865f, 0.49f, 0.595f, 0.665f, 0.738f, 0.815f, 0.875f),
        ),
    ),
    "legraise" to RigDef(
        listOf(
            P(0.17f, 0.640f, 0.24f, 0.620f, 0.30f, 0.560f, 0.33f, 0.640f, 0.47f, 0.620f, 0.640f, 0.740f, 0.790f, 0.880f),
            P(0.17f, 0.640f, 0.24f, 0.620f, 0.30f, 0.560f, 0.33f, 0.640f, 0.47f, 0.620f, 0.545f, 0.430f, 0.545f, 0.230f),
        ),
    ),
    "thrust" to RigDef(
        listOf(
            P(0.14f, 0.560f, 0.24f, 0.590f, 0.28f, 0.660f, 0.30f, 0.720f, 0.48f, 0.730f, 0.615f, 0.730f, 0.700f, 0.900f),
            P(0.14f, 0.560f, 0.24f, 0.590f, 0.30f, 0.610f, 0.42f, 0.570f, 0.46f, 0.530f, 0.595f, 0.640f, 0.700f, 0.900f),
        ),
        bar = 1,
        bench = true,
    ),
    "lunge" to RigDef(
        listOf(
            STAND,
            P(0.49f, 0.165f, 0.49f, 0.270f, 0.455f, 0.400f, 0.430f, 0.500f, 0.50f, 0.560f, 0.630f, 0.690f, 0.665f, 0.905f),
        ),
    ),
    "calf" to RigDef(
        listOf(
            STAND,
            P(0.50f, 0.065f, 0.50f, 0.175f, 0.465f, 0.310f, 0.435f, 0.420f, 0.520f, 0.450f, 0.545f, 0.660f, 0.550f, 0.870f),
        ),
    ),
    "shrug" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.465f, 0.360f, 0.435f, 0.470f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.080f, 0.50f, 0.200f, 0.465f, 0.355f, 0.435f, 0.470f, 0.520f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "jump" to RigDef(
        listOf(
            STAND,
            P(0.45f, 0.215f, 0.46f, 0.320f, 0.38f, 0.380f, 0.345f, 0.470f, 0.47f, 0.615f, 0.615f, 0.670f, 0.560f, 0.905f),
            P(0.50f, 0.010f, 0.51f, 0.115f, 0.575f, 0.050f, 0.545f, -0.040f, 0.52f, 0.380f, 0.585f, 0.560f, 0.545f, 0.720f),
        ),
    ),
    "run" to RigDef(
        listOf(
            P(0.48f, 0.120f, 0.50f, 0.230f, 0.575f, 0.330f, 0.635f, 0.400f, 0.52f, 0.500f, 0.625f, 0.650f, 0.720f, 0.790f),
            P(0.48f, 0.110f, 0.50f, 0.220f, 0.575f, 0.320f, 0.635f, 0.390f, 0.52f, 0.490f, 0.545f, 0.640f, 0.520f, 0.880f),
            P(0.48f, 0.120f, 0.50f, 0.230f, 0.415f, 0.330f, 0.355f, 0.410f, 0.52f, 0.500f, 0.425f, 0.640f, 0.360f, 0.760f),
            P(0.48f, 0.110f, 0.50f, 0.220f, 0.415f, 0.320f, 0.355f, 0.400f, 0.52f, 0.490f, 0.545f, 0.635f, 0.600f, 0.835f),
        ),
    ),
    "carry" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.460f, 0.365f, 0.450f, 0.475f, 0.520f, 0.500f, 0.560f, 0.710f, 0.590f, 0.915f),
            P(0.50f, 0.108f, 0.50f, 0.218f, 0.462f, 0.358f, 0.452f, 0.468f, 0.520f, 0.492f, 0.530f, 0.700f, 0.540f, 0.908f),
        ),
    ),
    "pulldown" to RigDef(
        listOf(
            P(0.50f, 0.190f, 0.50f, 0.290f, 0.470f, 0.150f, 0.505f, 0.055f, 0.52f, 0.560f, 0.545f, 0.760f, 0.550f, 0.915f),
            P(0.50f, 0.165f, 0.50f, 0.265f, 0.450f, 0.360f, 0.445f, 0.430f, 0.52f, 0.560f, 0.545f, 0.760f, 0.550f, 0.915f),
        ),
    ),
    "kickback" to RigDef(
        listOf(
            P(0.28f, 0.290f, 0.31f, 0.360f, 0.34f, 0.450f, 0.330f, 0.540f, 0.46f, 0.530f, 0.555f, 0.700f, 0.565f, 0.915f),
            P(0.28f, 0.290f, 0.31f, 0.360f, 0.28f, 0.430f, 0.215f, 0.470f, 0.46f, 0.530f, 0.555f, 0.700f, 0.565f, 0.915f),
        ),
    ),
    "stretch" to RigDef(
        listOf(
            P(0.50f, 0.130f, 0.50f, 0.240f, 0.560f, 0.150f, 0.590f, 0.060f, 0.52f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.46f, 0.125f, 0.47f, 0.235f, 0.520f, 0.145f, 0.555f, 0.055f, 0.52f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "twist" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.555f, 0.330f, 0.610f, 0.350f, 0.52f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.425f, 0.330f, 0.370f, 0.350f, 0.52f, 0.500f, 0.545f, 0.710f, 0.550f, 0.915f),
        ),
    ),
    "balance" to RigDef(
        listOf(
            P(0.50f, 0.115f, 0.50f, 0.225f, 0.555f, 0.340f, 0.610f, 0.370f, 0.52f, 0.500f, 0.470f, 0.640f, 0.430f, 0.520f),
            P(0.50f, 0.108f, 0.50f, 0.220f, 0.560f, 0.335f, 0.615f, 0.362f, 0.52f, 0.495f, 0.475f, 0.645f, 0.435f, 0.528f),
        ),
    ),
)

/** Pattern display names (used by the Library "Pattern" grouping mode). */
val PATTERN_LABELS = mapOf(
    "squat" to "Squat", "hinge" to "Hinge", "bench" to "Bench", "press" to "Press",
    "pushup" to "Push-up / Dip", "pullup" to "Pull-up", "row" to "Row", "curl" to "Curl",
    "pushdown" to "Pushdown", "raise" to "Raise", "fly" to "Fly", "crunch" to "Crunch",
    "plank" to "Plank / Hold", "legraise" to "Leg Raise", "thrust" to "Hip Thrust",
    "lunge" to "Lunge", "calf" to "Calf", "shrug" to "Shrug", "jump" to "Jump",
    "run" to "Run / Cardio", "carry" to "Carry", "pulldown" to "Pulldown", "kickback" to "Kickback",
    "stretch" to "Stretch / Mobility", "twist" to "Twist", "balance" to "Balance", "stand" to "Other",
)

/**
 * patternFor — deterministic exercise → rig mapping. Keyword rules run most-specific-first;
 * falls back through equipment, then primary muscle, then "stand".
 */
fun patternFor(name: String, equipment: String, muscles: List<String>): String {
    val n = name.lowercase()
    val e = equipment.lowercase()
    return when {
        n.contains("pull-up") || n.contains("pullup") || n.contains("chin-up") || n.contains("chinup") || n.contains("muscle up") -> "pullup"
        n.contains("lat pulldown") || n.contains("pulldown") || n.contains("pull-down") -> "pulldown"
        n.contains("pullover") || n.contains("pull over") -> "pulldown"
        n.contains("deadlift") -> "hinge"
        n.contains("good morning") -> "hinge"
        n.contains("squat") && (n.contains("jump") || n.contains("thruster")) -> "jump"
        n.contains("squat") -> "squat"
        n.contains("lunge") || n.contains("split squat") || n.contains("step-up") || n.contains("step up") -> "lunge"
        n.contains("bench press") -> "bench"
        n.contains("push-up") || n.contains("pushup") || n.contains("push up") -> "pushup"
        n.contains("dip") -> "pushup"
        n.contains("overhead press") || n.contains("shoulder press") || n.contains("military press") || n.contains("ohp") -> "press"
        n.contains("thruster") || n.contains("clean") || n.contains("snatch") -> "press"
        n.contains("row") -> "row"
        n.contains("face pull") -> "row"
        n.contains("leg curl") -> "legraise"
        n.contains("curl") -> "curl"
        n.contains("pushdown") || n.contains("push-down") || n.contains("triceps extension") || n.contains("triceps press") || n.contains("skull") -> "pushdown"
        n.contains("calf") -> "calf"
        n.contains("kickback") -> "kickback"
        n.contains("leg raise") || n.contains("knee raise") || n.contains("toes to bar") || n.contains("hanging leg") -> "legraise"
        n.contains("lateral raise") || n.contains("front raise") || n.contains("rear delt") || n.contains("raise") -> "raise"
        n.contains("fly") || n.contains("flye") || n.contains("chest press") -> if (n.contains("floor")) "bench" else "fly"
        n.contains("crunch") || n.contains("sit-up") || n.contains("situp") || n.contains("ab wheel") -> "crunch"
        n.contains("plank") || n.contains("hollow") || n.contains("isometric") || n.contains("hold") -> "plank"
        n.contains("thrust") || n.contains("bridge") -> "thrust"
        n.contains("shrug") -> "shrug"
        n.contains("jump") || n.contains("burpee") || n.contains("bounding") || n.contains("plyo") -> "jump"
        n.contains("run") || n.contains("sprint") || n.contains("jog") || n.contains("bike") || n.contains("cycl") || n.contains("row") || n.contains("skip") || n.contains("rope") || n.contains("cardio") -> "run"
        n.contains("carry") || n.contains("farmer") || n.contains("walk") || n.contains("sled") -> "carry"
        n.contains("stretch") || n.contains("mobility") || n.contains("yoga") || n.contains("foam") -> "stretch"
        n.contains("twist") || n.contains("rotation") || n.contains("pallof") -> "twist"
        n.contains("balance") || n.contains("single-leg") || n.contains("pistol") || n.contains("stand") -> "balance"
        n.contains("press") -> "press"
        n.contains("extension") -> if (muscles.any { it.contains("tri") }) "pushdown" else if (muscles.any { it.contains("back") || it.contains("hip") }) "hinge" else "curl"
        n.contains("pull") -> "row"
        e == "barbell" || e == "ez bar" -> if (muscles.any { it.contains("quad") || it.contains("ham") || it.contains("glut") }) "squat" else "press"
        e == "kettlebell" -> if (muscles.any { m -> m.contains("ham") || m.contains("glut") }) "hinge" else "press"
        else -> byMuscle(muscles)
    }
}

private fun byMuscle(muscles: List<String>): String {
    val m = muscles.joinToString(" ").lowercase()
    return when {
        m.contains("quad") -> "squat"
        m.contains("hamstring") || m.contains("glute") || m.contains("hip") -> "hinge"
        m.contains("calf") -> "calf"
        m.contains("chest") -> "bench"
        m.contains("lat") || m.contains("upper back") || m.contains("back") || m.contains("trap") -> "row"
        m.contains("shoulder") || m.contains("delt") -> "press"
        m.contains("biceps") || m.contains("forearm") -> "curl"
        m.contains("triceps") -> "pushdown"
        m.contains("ab") || m.contains("core") || m.contains("oblique") -> "crunch"
        m.contains("cardio") || m.contains("conditioning") -> "run"
        else -> "stand"
    }
}

/** Muscle-group → art hue (UI expert technique 2; substring match absorbs legacy id variants). */
private val MUSCLE_HUES = listOf(
    "chest" to 0xFF4E9BFF, "shoulder" to 0xFF22D3EE, "delt" to 0xFF22D3EE,
    "biceps" to 0xFFFB7185, "forearm" to 0xFFF59E0B, "triceps" to 0xFFF472B6,
    "lat" to 0xFF818CF8, "upper_back" to 0xFF818CF8, "trap" to 0xFF818CF8, "back" to 0xFF818CF8,
    "lower_back" to 0xFFFB923C, "quad" to 0xFFA3E635, "hamstring" to 0xFF2DD4BF,
    "glute" to 0xFFFB7185, "calf" to 0xFF6EE7B7, "ab" to 0xFFFBBF24, "core" to 0xFFFBBF24,
    "oblique" to 0xFFFBBF24, "neck" to 0xFF38BDF8, "hand" to 0xFF38BDF8, "feet" to 0xFF6EE7B7,
    "ankle" to 0xFF6EE7B7, "cardio" to 0xFFF87171, "conditioning" to 0xFFF87171,
    "mobility" to 0xFFC4B5FD, "balance" to 0xFFC4B5FD, "full_body" to 0xFF4E9BFF, "hip" to 0xFFFB7185,
)

fun muscleHue(muscles: List<String>, fallback: Color = Color(0xFF4E9BFF)): Color {
    val joined = muscles.joinToString(" ").lowercase()
    for ((k, v) in MUSCLE_HUES) if (joined.contains(k)) return Color(v)
    return fallback
}

/** Convenience overload for library entries. */
fun patternFor(ex: LibraryExercise): String = patternFor(ex.name, ex.equipment, ex.muscles)

private fun smooth(f: Float): Float = f * f * (3f - 2f * f)

private fun lerpPose(a: FloatArray, b: FloatArray, f: Float): FloatArray =
    FloatArray(a.size) { i -> a[i] + (b[i] - a[i]) * f }

private fun poseAt(rig: RigDef, t: Float): FloatArray {
    val n = rig.keys.size
    if (n == 1) return rig.keys[0]
    val m = 2 * (n - 1)
    val s = (t % 1f) * m
    val tt = if (s < n - 1) s else m - s
    val seg = min(tt.toInt(), n - 2)
    val frac = smooth(tt - seg)
    return lerpPose(rig.keys[seg], rig.keys[seg + 1], frac)
}

/**
 * RigArt — the animated exercise pictogram. Side-view stick athlete + equipment glyph on a
 * muscle-hued breathing glow. `seed` desyncs cards from each other; `playing=false` freezes.
 */
@Composable
fun RigArt(
    pattern: String,
    equipment: String,
    hue: Color,
    seed: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    playing: Boolean = true,
) {
    val rig = remember(pattern) { RIG_PATTERNS[pattern] ?: RIG_PATTERNS.getValue("stand") }
    val shared = LocalRigClock.current
    val own = if (shared == null && playing) rememberRigClock() else null
    val bg = remember(hue) {
        Brush.verticalGradient(listOf(hue.copy(alpha = 0.20f), hue.copy(alpha = 0.05f), Color.Transparent))
    }
    Canvas(modifier.size(size)) {
        val t: Float = if (!playing) 0.30f else (shared ?: own)!!.value
        drawRig(rig, pattern, equipment, hue, seed, t, bg)
    }
}

private fun DrawScope.drawRig(rig: RigDef, pattern: String, equipment: String, hue: Color, seed: Int, t: Float, bg: Brush) {
    val w = size.width
    val h = size.height
    val phase = (t + (seed % 97) / 97f * 0.31f) % 1f
    val pose = poseAt(rig, phase)

    // backdrop
    drawRect(bg)
    val breathe = 0.5f + 0.5f * sin(phase * 2f * PI.toFloat())
    drawCircle(
        Brush.radialGradient(
            listOf(hue.copy(alpha = 0.16f + 0.08f * breathe), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.46f),
            radius = min(w, h) * 0.52f,
        ),
        radius = min(w, h) * 0.52f,
        center = Offset(w * 0.5f, h * 0.46f),
    )
    // orbiting dust
    for (i in 0 until 3) {
        val a = phase * 2f * PI.toFloat() + i * 2.09f + seed * 0.11f
        val rr = min(w, h) * (0.30f + 0.045f * sin(phase * 2f * PI.toFloat() + i))
        drawCircle(
            hue.copy(alpha = 0.14f),
            radius = w * 0.012f,
            center = Offset(w * 0.5f + cos(a) * rr, h * 0.48f + sin(a) * rr * 0.72f),
        )
    }

    // bench + ground shadow
    if (rig.bench) {
        drawRoundRect(
            hue.copy(alpha = 0.30f),
            topLeft = Offset(w * 0.10f, h * 0.635f),
            size = Size(w * 0.52f, h * 0.045f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f),
        )
    }
    val shadowY = if (rig.bench || rig.topBar) h * 0.94f else pose[13] * h + h * 0.028f
    drawOval(
        Color.Black.copy(alpha = 0.18f),
        topLeft = Offset(w * 0.28f, shadowY - h * 0.014f),
        size = Size(w * 0.44f, h * 0.028f),
    )

    val limb = w * 0.050f
    val farX = w * 0.045f
    val farY = h * 0.012f
    val dim = hue.copy(alpha = 0.40f)
    val solid = hue.copy(alpha = 0.95f)

    fun j(i: Int) = Offset(pose[i * 2] * w, pose[i * 2 + 1] * h)
    val head = j(0); val neck = j(1); val elbow = j(2); val wrist = j(3)
    val hip = j(4); val knee = j(5); val ankle = j(6)

    // far limbs (behind)
    drawLine(dim, neck + Offset(farX, farY), Offset(elbow.x + farX, elbow.y + farY), limb * 0.85f, StrokeCap.Round)
    drawLine(dim, Offset(elbow.x + farX, elbow.y + farY), Offset(wrist.x + farX, wrist.y + farY), limb * 0.85f, StrokeCap.Round)
    drawLine(dim, hip + Offset(farX, farY), Offset(knee.x + farX, knee.y + farY), limb * 0.85f, StrokeCap.Round)
    drawLine(dim, Offset(knee.x + farX, knee.y + farY), Offset(ankle.x + farX, ankle.y + farY), limb * 0.85f, StrokeCap.Round)

    // torso
    drawLine(solid, neck, hip, limb * 1.25f, StrokeCap.Round)
    // near limbs
    drawLine(solid, neck, elbow, limb, StrokeCap.Round)
    drawLine(solid, elbow, wrist, limb, StrokeCap.Round)
    drawLine(solid, hip, knee, limb, StrokeCap.Round)
    drawLine(solid, knee, ankle, limb, StrokeCap.Round)
    // head
    drawCircle(solid, w * 0.052f, head)

    // equipment glyphs
    val anchor = when (rig.bar) { 2 -> neck; 1 -> wrist; else -> null }
    when (equipment.lowercase()) {
        "barbell", "ez bar", "trap bar" -> anchor?.let { drawBar(it, w, hue) }
        "dumbbell" -> anchor?.let { drawDumbbell(it, w, hue) }
        "kettlebell" -> anchor?.let { drawKettlebell(it, w, hue) }
        "cable", "machine" -> drawCable(wrist, w, h, hue, topAnchor = rig.topBar || pattern == "pushdown" || pattern == "pulldown")
        "band" -> drawBand(wrist, ankle, w, h, hue)
        else -> { /* bodyweight / other: nothing held */ }
    }
    if (rig.topBar) {
        drawLine(hue.copy(alpha = 0.65f), Offset(w * 0.14f, h * 0.045f), Offset(w * 0.86f, h * 0.045f), limb * 0.7f, StrokeCap.Round)
    }
}

private fun DrawScope.drawBar(c: Offset, w: Float, hue: Color) {
    drawLine(hue, Offset(c.x - w * 0.17f, c.y), Offset(c.x + w * 0.17f, c.y), w * 0.035f, StrokeCap.Round)
    drawCircle(hue, w * 0.040f, Offset(c.x - w * 0.115f, c.y))
    drawCircle(hue, w * 0.040f, Offset(c.x + w * 0.115f, c.y))
}

private fun DrawScope.drawDumbbell(c: Offset, w: Float, hue: Color) {
    drawLine(hue, Offset(c.x - w * 0.055f, c.y), Offset(c.x + w * 0.055f, c.y), w * 0.028f, StrokeCap.Round)
    drawLine(hue, Offset(c.x - w * 0.055f, c.y - w * 0.032f), Offset(c.x - w * 0.055f, c.y + w * 0.032f), w * 0.022f, StrokeCap.Round)
    drawLine(hue, Offset(c.x + w * 0.055f, c.y - w * 0.032f), Offset(c.x + w * 0.055f, c.y + w * 0.032f), w * 0.022f, StrokeCap.Round)
}

private fun DrawScope.drawKettlebell(c: Offset, w: Float, hue: Color) {
    drawCircle(hue, w * 0.042f, Offset(c.x, c.y + w * 0.058f))
    drawArc(
        hue,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(c.x - w * 0.030f, c.y - w * 0.004f),
        size = Size(w * 0.060f, w * 0.052f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.016f, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawCable(wrist: Offset, w: Float, h: Float, hue: Color, topAnchor: Boolean) {
    val a = if (topAnchor) Offset(w * 0.72f, h * 0.03f) else Offset(w * 0.24f, h * 0.80f)
    drawLine(hue.copy(alpha = 0.55f), a, wrist, w * 0.016f)
    drawCircle(hue.copy(alpha = 0.75f), w * 0.020f, a)
}

private fun DrawScope.drawBand(wrist: Offset, ankle: Offset, w: Float, h: Float, hue: Color) {
    val a = Offset(ankle.x, h * 0.93f)
    drawLine(hue.copy(alpha = 0.5f), a, wrist, w * 0.018f)
}

/**
 * rememberCountUp — UI expert technique 9: one-shot count-up for headline numbers
 * (library size, Est. 1RM, header streak). Runs on first composition / target change.
 */
@Composable
fun rememberCountUp(target: Int, ms: Int = 700): Int {
    val a = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(target) {
        a.snapTo(0f)
        a.animateTo(1f, tween(ms, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    return (target * a.value).toInt()
}

/**
 * Modifier.streamIn — UI expert technique 4: staggered fade+slide on first composition
 * only (index-capped so below-fold items never wait). graphicsLayer = layer phase only.
 */
@Composable
fun Modifier.streamIn(index: Int): Modifier {
    val a = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index.coerceAtMost(8)) * 45L)
        a.animateTo(1f, tween(340, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    return this.graphicsLayer { alpha = a.value; translationY = (1f - a.value) * 26f }
}
