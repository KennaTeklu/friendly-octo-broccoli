package com.peakform.fitness.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.BuildConfig
import com.peakform.fitness.core.WorkoutExercise
import com.peakform.fitness.core.WorkoutRecord
import com.peakform.fitness.engine.Generator
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.engine.Sessions
import com.peakform.fitness.ProLog
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Workout — 1:1 port of #workout-section with the P4 card system (scr-workout.md):
 * chips row, collapsible details, P4 collapsible exercise cards (one open at a time,
 * state badges: Needs 1RM / Started / Ready / skipped / done), prescription block,
 * logger (weight stepper, sets, per-set reps, RPE chips), complete-workout flow,
 * rest-timer overlay, no-workout empty state.
 */

/**
 * BATCH-39c — Pure helper for the no-coach-but-dossier case.
 *
 * When the exercise has no authored coach (or the coach's requirements() all
 * return false), the card used to always render "No prescription available
 * — no coach requirements declared." even for exercises whose dossier exists
 * with a PeakForm landmark. Now: when the dossier has a goal with a label
 * or landmark, render "PeakForm standard — no coach yet" instead — the
 * dossier tier block above the prescription already showed the goal and
 * landmark, so this string tells the user that the dossier layer is the
 * source of truth while a coach is being authored.
 *
 * Returns null when the requirements branch shouldn't fire (i.e. at least
 * one requirement is true) — the card's outer if/else handles that path.
 */
object WorkoutCardMessages {
    const val NO_COACH_DOSSIER_PRESENT = "PeakForm standard — no coach yet"
    const val NO_COACH_NO_DOSSIER = "No prescription available — no coach requirements declared."

    fun noCoachMessage(
        dossier: com.peakform.fitness.coach.dossiers.ExerciseDossier?,
    ): String {
        val hasGoal = dossier?.goals?.firstOrNull()?.let {
            it.landmark.isNotBlank() || it.label.isNotBlank()
        } == true
        return if (hasGoal) NO_COACH_DOSSIER_PRESENT else NO_COACH_NO_DOSSIER
    }
}

/**
 * BATCH-39c — Pure helper for the regenerate button label/state.
 *
 * `buttonLabel(regenerating)` returns the button text. `buttonEnabled(regenerating)`
 * returns whether the button is clickable. `showsProgress(regenerating)`
 * returns whether the in-screen progress indicator should be visible. These
 * exist as testable pure functions — the card itself just calls them with
 * the live `regenerating` state.
 */
object RegenerateButtonState {
    fun buttonLabel(regenerating: Boolean): String =
        if (regenerating) "Regenerating…" else "Regenerate"
    fun buttonEnabled(regenerating: Boolean): Boolean = !regenerating
    fun showsProgress(regenerating: Boolean): Boolean = regenerating
}

/**
 * BATCH-39e-2 — Progress overlay that replaces each Regenerate button while
 * `regenerating == true`. The overlay occupies the exact slot where the
 * button was (the caller passes the same `modifier` it would have passed
 * to P4Button — typically `Modifier.weight(1f)` for the top button and
 * `Modifier.fillMaxWidth()` for the bottom button).
 *
 * Layout (matches the spec's Column shape verbatim):
 *   - Row: CircularProgressIndicator (16dp, accent) + Spacer + Text(phase)
 *   - LinearProgressIndicator: determinate when genTotal > 1, indeterminate
 *     when genTotal <= 1
 *   - Text: "$genStep of $genTotal"
 *
 * Tint tokens: c.accent for the bar/spinner fill, c.tintLine for the track,
 * c.tintBg for the background — all from v38b's tint system.
 *
 * When `genPhase == null` the header text falls back to "Building your
 * workout" (the v39c fallback). When `genTotal <= 1` (a fast path with no
 * stable step count) the LinearProgressIndicator renders in indeterminate
 * mode — `progress` parameter is omitted.
 */
@Composable
fun GenerateProgressOverlay(
    genPhase: String?,
    genStep: Int,
    genTotal: Int,
    modifier: Modifier = Modifier,
) {
    val c = LocalProColors.current
    val indeterminate = genTotal <= 1
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.tintBg)
            .border(1.dp, c.tintLine, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = c.accent,
                strokeWidth = 2.dp,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = genPhase ?: "Building your workout",
                style = ProType.body,
                color = c.text,
            )
        }
        if (indeterminate) {
            androidx.compose.material3.LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                color = c.accent,
                trackColor = c.tintLine,
            )
        } else {
            androidx.compose.material3.LinearProgressIndicator(
                progress = { (genStep.toFloat() / genTotal.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                color = c.accent,
                trackColor = c.tintLine,
            )
        }
        Text(
            text = "$genStep of $genTotal",
            style = ProType.small,
            color = c.text3,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/**
 * BATCH-39e-2 — Pure helper for the overlay's visible/enabled state.
 *
 * `overlayVisibleForButton(regenerating, genPhase)` returns true when the
 * overlay should replace a Regenerate button (regenerating is true AND a
 * phase has been reported — i.e. genPhase != null). When this returns
 * false, the button itself renders normally.
 *
 * `progressFraction(genStep, genTotal)` returns the 0..1 fraction for the
 * determinate LinearProgressIndicator. Returns 0f for the indeterminate
 * branch (the UI ignores it there).
 */
object GenerateOverlayState {
    fun overlayVisibleForButton(regenerating: Boolean, genPhase: String?): Boolean =
        regenerating && genPhase != null

    fun progressFraction(genStep: Int, genTotal: Int): Float =
        if (genTotal > 1) (genStep.toFloat() / genTotal.toFloat()).coerceIn(0f, 1f) else 0f

    fun isIndeterminate(genTotal: Int): Boolean = genTotal <= 1
}

@Composable
fun WorkoutScreen(
    onRequireGenerate: () -> Unit,
    onOpenSection: (String) -> Unit,
) {
    val c = LocalProColors.current
    val scope = rememberCoroutineScope()
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose {
            synchronized(ProState.listeners) { ProState.listeners.remove(l) }
        }
    }
    val _verTick = ver.intValue
    val scrollState = rememberScrollState()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var prToast by remember { mutableStateOf<String?>(null) }

    // BATCH-38d: backup launcher — same pattern as Settings.kt.
    // On tap, generate the backup JSON via Backup.exportCompleteV1(ctx),
    // then launch the system file picker to save it.
    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val data = com.peakform.fitness.core.Backup.exportCompleteV1(ctx)
            com.peakform.fitness.core.Backup.writeJsonToUri(ctx, uri, data)
            prToast = "Backup saved"
        }
    }

    // BATCH-38d: grip sheet state
    var showGripSheet by remember { mutableStateOf(false) }

    var openCardIndex by remember { mutableIntStateOf(-1) }
    var showDetails by remember { mutableStateOf(false) }
    var completeDialog by remember { mutableStateOf(false) }
    var celebration by remember { mutableStateOf<Sessions.CompletionResult?>(null) }
    // FIX (audit): remove/skip now require confirmation (engine ships the verbatim strings)
    var confirmRemove by remember { mutableStateOf<Int?>(null) }
    var confirmSkip by remember { mutableStateOf<Int?>(null) }
    // 1.4: share sheet (SH1–SH4) + BIGGER/STRONGER momentum steering (C3/C4)
    var showShare by remember { mutableStateOf(false) }
    var showMomentum by remember { mutableStateOf(false) }
    // BATCH-2: regenerate confirm (top + bottom buttons share the same flow) + replace confirm
    var confirmRegenerate by remember { mutableStateOf(false) }
    var confirmReplace by remember { mutableStateOf<Int?>(null) }
    // BATCH-39c: regenerating flag drives the in-screen progress indicator + disables the
    // Regenerate buttons while Generator.performGenerateWorkout is running.
    // BATCH-39e-2: granular progress state — genPhase/genStep/genTotal drive the
    // in-button overlay (replaces the v39c spinner + label swap). The overlay
    // renders in the exact slot where each Regenerate button was, so the user
    // sees progress at the position they tapped.
    var regenerating by remember { mutableStateOf(false) }
    var genPhase by remember { mutableStateOf<String?>(null) }
    var genStep by remember { mutableStateOf(0) }
    var genTotal by remember { mutableStateOf(1) }

    val workout = ProState.currentWorkout
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Today's Workout", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            // BATCH-2: top regenerate button (regenerateWorkoutBtn, same flow as bottom)
            // BATCH-39e-2: when regenerating, replace the button with the
            // GenerateProgressOverlay composable. The overlay occupies the
            // exact slot where the button was — it does NOT scroll with the
            // page (it's in the same Row position as the button was, anchored
            // to the title row).
            if (regenerating) {
                GenerateProgressOverlay(
                    genPhase = genPhase,
                    genStep = genStep,
                    genTotal = genTotal,
                    modifier = Modifier.weight(1f),
                )
            } else if (workout != null && workout.exercises.isNotEmpty()) {
                P4Button(
                    RegenerateButtonState.buttonLabel(regenerating),
                    icon = if (regenerating) "fa-spinner" else "fa-rotate",
                    style = BtnStyle.GHOST, minHeight = 34,
                    enabled = RegenerateButtonState.buttonEnabled(regenerating),
                    onClick = { confirmRegenerate = true },
                )
            }
        }

        // chips row
        if (workout != null && workout.exercises.isNotEmpty()) {
            // FIX (audit): chipData was recomputed 5× per render — hoist to a single val.
            val chips = remember(workout.id, ver.intValue) { Generator.chipData(workout) }
            if (chips != null) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    P4Chip(chips.name, icon = "fa-dumbbell")
                    P4Chip(chips.focus, icon = "fa-crosshairs")
                    P4Chip("${chips.estTime}-${chips.estTime + 15} min", icon = "fa-clock")
                    P4Chip(chips.intensity, icon = "fa-gauge-high")
                    if (chips.deload && notifFlag("deload_notice")) P4Chip("Deload", icon = "fa-leaf", tint = c.warn)
                    if (chips.taper) P4Chip("Taper", icon = "fa-feather", tint = c.ok)
                }
            }

            // details collapsible + Share (SH1–SH4)
            GlassCard(padding = PaddingValues(14.dp), onClick = { showDetails = !showDetails }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("View details", style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
                    Text(
                        "Share", style = ProType.label, color = c.accent,
                        modifier = Modifier.clickable { showShare = true }.padding(horizontal = 10.dp),
                    )
                    com.peakform.fitness.ui.FaIcon(if (showDetails) "fa-chevron-up" else "fa-chevron-down", size = 13.sp, tint = c.accent)
                }
                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        KeyValueRow("Workout Type", workout.name)
                        KeyValueRow("Focus", chips?.focus ?: "Full Body")
                        KeyValueRow("Estimated Time", "${(chips?.estTime ?: 0)}-${(chips?.estTime ?: 0) + 15} minutes")
                        KeyValueRow("Intensity", chips?.intensity ?: "Moderate")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Rest 60–90 seconds between sets. Warm up before your first exercise, stretch after.",
                            style = ProType.small, color = c.text3,
                        )
                    }
                }
            }

            // exercise cards
            workout.exercises.forEachIndexed { index, ex ->
                ExerciseCard(
                    index = index,
                    exercise = ex,
                    workoutId = workout.id,
                    expanded = openCardIndex == index,
                    onToggle = { openCardIndex = if (openCardIndex == index) -1 else index },
                    onLogged = {
                        ver.intValue++
                        ProState.notifyChanged()
                        // FIX (audit): the haptics power toggle persisted but nothing read it —
                        // buzz on every logged effort now.
                        if (powerFlag("haptics", default = true)) {
                            try {
                                val v = com.peakform.fitness.ui.Fa.appContext?.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                                v?.vibrate(android.os.VibrationEffect.createOneShot(18, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                            } catch (_: Exception) {}
                        }
                        // FIX (audit): rest timer now honors the autoRest power setting and lives
                        // in the app-scope RestTimer holder (survives tab switches).
                        if (powerFlag("autoRest", default = true)) {
                            RestTimer.start(ProState.data.user.settings.restTime)
                        }
                        val w = ProState.currentWorkout
                        if (w != null && w.exercises.all { it.isLogged }) {
                            completeDialog = true
                        } else {
                            // advance to next unlogged
                            openCardIndex = w?.exercises?.indexOfFirst { !it.isLogged } ?: -1
                        }
                    },
                    onSkip = { notes ->
                        val w = ProState.currentWorkout ?: return@ExerciseCard
                        ProState.currentWorkout = Sessions.skipExercise(w, index, notes)
                        ProState.performSave()
                        ver.intValue++
                        ProState.notifyChanged()
                    },
                    onRemove = {
                        // FIX (audit): one accidental tap deleted the exercise — confirm first.
                        confirmRemove = index
                    },
                    onAskSkip = {
                        confirmSkip = index
                    },
                    onStartRest = { sec -> RestTimer.start(sec) },
                    // BATCH-2: replace (confirm) + resume (clear actual, re-open logger)
                    onAskReplace = { confirmReplace = index },
                    onResume = {
                        val w = ProState.currentWorkout ?: return@ExerciseCard
                        val cleared = w.exercises.mapIndexed { i, e ->
                            if (i == index) e.copy(actual = null, skipped = false) else e
                        }
                        ProState.currentWorkout = w.copy(exercises = cleared)
                        ProState.performSave()
                        openCardIndex = index
                        ver.intValue++
                        ProState.notifyChanged()
                    },
                )
            }

            // BATCH-2: bottom regenerate button (bottomRegenerateWorkoutBtn — same flow as top)
            // BATCH-39e-2: when regenerating, replace the button with the
            // GenerateProgressOverlay composable. The overlay occupies the
            // exact slot where the button was — full-width, in normal flow
            // (scrolls with the page because it lives inside the workout
            // Column, but at the bottom of the exercise list it's typically
            // in view when the user taps Regenerate).
            if (regenerating) {
                GenerateProgressOverlay(
                    genPhase = genPhase,
                    genStep = genStep,
                    genTotal = genTotal,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                P4Button(
                    "Regenerate Workout",
                    icon = "fa-rotate",
                    style = BtnStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = RegenerateButtonState.buttonEnabled(regenerating),
                ) {
                    confirmRegenerate = true
                }
            }

            // complete button
            val allLogged = workout.exercises.isNotEmpty() && workout.exercises.all { it.isLogged }
            if (allLogged) {
                LuxuryStartButton("Complete & Log Today's Workout", "fa-trophy", onClick = {
                    completeDialog = true
                }, modifier = Modifier.fillMaxWidth())
            }
        } else {
            EmptyState(
                icon = "fa-dumbbell",
                title = "No Workout Scheduled",
                message = "Generate a workout to get started with today's training.",
            )
            Spacer(Modifier.height(4.dp))
            P4Button("Generate Workout", onClick = onRequireGenerate, icon = "fa-bolt", modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(16.dp))
    }

    // BATCH-38c/38d: Floating action rail — 5 vertical rounded-square buttons on the
    // right side of the workout screen (timer, cloud-download, cloud-upload, hand, arrow-up).
    FloatingActionRail(
        onTimer = { RestTimer.start(ProState.data.user.settings.restTime) },
        // BATCH-38d: backup wired — launches file picker, saves Backup.exportCompleteV1
        onDownload = { backupLauncher.launch("peakform-backup-${System.currentTimeMillis()}.json") },
        onUpload = { onOpenSection("import") },
        // BATCH-38d: grip wired — opens GripEquipmentSheet
        onGrip = { showGripSheet = true },
        onScrollTop = { scope.launch { scrollState.scrollTo(0) } },
    )

    // ---- rest timer overlay (app-scope — survives navigation) ----
    if (RestTimer.running) {
        RestTimerOverlay(
            secondsLeft = RestTimer.secondsLeft,
            total = RestTimer.total,
            onAdd15 = { RestTimer.add15() },
            onDone = { RestTimer.stop() },
        )
    }

    // ---- remove confirmation (verbatim engine strings) ----
    confirmRemove?.let { idx ->
        val w = ProState.currentWorkout
        val name = w?.exercises?.getOrNull(idx)?.name ?: ""
        SwalDialog(
            title = Sessions.REMOVE_TITLE,
            text = Sessions.removeConfirmText(name),
            confirmText = "Remove",
            cancelText = "Cancel",
            onConfirm = {
                confirmRemove = null
                val cur = ProState.currentWorkout ?: return@SwalDialog
                ProState.currentWorkout = Sessions.removeExerciseFromWorkout(cur, idx)
                ProState.performSave()
                if (openCardIndex == idx) openCardIndex = -1
                ver.intValue++
                ProState.notifyChanged()
            },
            onDismiss = { confirmRemove = null },
        )
    }

    // ---- skip confirmation ----
    confirmSkip?.let { idx ->
        val w = ProState.currentWorkout
        val name = w?.exercises?.getOrNull(idx)?.name ?: ""
        SwalDialog(
            title = Sessions.SKIP_TITLE,
            text = Sessions.skipConfirmText(name),
            confirmText = "Skip it",
            cancelText = "Cancel",
            onConfirm = {
                confirmSkip = null
                val cur = ProState.currentWorkout ?: return@SwalDialog
                ProState.currentWorkout = Sessions.skipExercise(cur, idx, "Skipped - user choice")
                ProState.performSave()
                ver.intValue++
                ProState.notifyChanged()
            },
            onDismiss = { confirmSkip = null },
        )
    }

    // ---- BATCH-2: replace confirmation (swaps the exercise for a fresh pick) ----
    confirmReplace?.let { idx ->
        val w = ProState.currentWorkout
        val name = w?.exercises?.getOrNull(idx)?.name ?: ""
        SwalDialog(
            title = "Replace Exercise?",
            text = "Replace \"$name\" with a different exercise for the same muscle group? The current exercise will be removed from this workout.",
            confirmText = "Replace",
            cancelText = "Keep it",
            onConfirm = {
                confirmReplace = null
                val cur = ProState.currentWorkout ?: return@SwalDialog
                ProState.currentWorkout = Sessions.removeExerciseFromWorkout(cur, idx)
                ProState.performSave()
                if (openCardIndex == idx) openCardIndex = -1
                ver.intValue++
                ProState.notifyChanged()
            },
            onDismiss = { confirmReplace = null },
        )
    }

    // ---- BATCH-2: regenerate confirmation (top + bottom button share this flow) ----
    if (confirmRegenerate) {
        SwalDialog(
            title = "Regenerate Workout?",
            text = "This will replace today's workout with a fresh one based on your current fatigue, recovery, and progression. Any logged sets will be lost.",
            confirmText = "Regenerate",
            cancelText = "Cancel",
            onConfirm = {
                confirmRegenerate = false
                // BATCH-39c: launch on Dispatchers.Default — the generator walks
                // 1,413 exercises, runs difficulty gates, and reconciles coaches
                // (>5s of CPU). Internal generator writes route to IO via
                // withContext(Dispatchers.IO); notifyChanged routes to Main via
                // withContext(Dispatchers.Main). The `regenerating` flag drives
                // the in-screen progress indicator + disables the buttons.
                //
                // BATCH-39e-2: pass an onProgress callback that updates the
                // genPhase/genStep/genTotal Compose states via
                // withContext(Dispatchers.Main). Both Regenerate buttons then
                // render the GenerateProgressOverlay composable in their slot.
                regenerating = true
                genPhase = null
                genStep = 0
                genTotal = com.peakform.fitness.engine.Generator.ProgressPhases.TOTAL
                scope.launch(Dispatchers.Default) {
                    try {
                        val result = Generator.performGenerateWorkout(suppressConfirm = true) { phase, step, total ->
                            withContext(Dispatchers.Main) {
                                genPhase = phase
                                genStep = step
                                genTotal = total
                            }
                        }
                        withContext(Dispatchers.IO) {
                            ProState.currentWorkout = result.workout
                            ProState.performSave()
                        }
                        withContext(Dispatchers.Main) {
                            ProState.notifyChanged()
                            ver.intValue++
                            regenerating = false
                            genPhase = null
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            regenerating = false
                            genPhase = null
                            ProLog.w("WORKOUT", "regenerate failed: ${e.message}")
                        }
                    }
                }
            },
            onDismiss = { confirmRegenerate = false },
        )
    }

    // ---- complete confirmation ----
    if (completeDialog && workout != null) {
        val unlogged = Sessions.unloggedExercises(workout)
        SwalDialog(
            title = if (unlogged.isEmpty()) "Complete Workout 🏆" else "⚠️ Unlogged Exercises",
            text = if (unlogged.isEmpty()) "Log this session and update your progression?"
            else "These exercises are not logged: ${unlogged.joinToString(", ")}. Complete anyway?",
            confirmText = "Yes, complete it",
            cancelText = "No, go back",
            onConfirm = {
                completeDialog = false
                scope.launch {
                    val result = Sessions.completeWorkout(workout, generateNext = true)
                    celebration = result
                    // BATCH-39a: build and append a SessionRecord from the completed workout.
                    // Only exercises with at least one completed set are included.
                    // Skipped exercises are included with skipped=true.
                    val sessionExercises = workout.exercises.map { ex ->
                        val actual = ex.actual
                        if (ex.skipped) {
                            com.peakform.fitness.core.state.StateModel.ExerciseRecord(
                                exerciseId = ex.id,
                                sets = emptyList(),
                                skipped = true,
                            )
                        } else if (actual != null && actual.sets > 0) {
                            val setRecords = (0 until actual.sets).map { i ->
                                com.peakform.fitness.core.state.StateModel.SetRecord(
                                    reps = actual.reps.getOrNull(i)?.toInt() ?: 0,
                                    load = actual.weight,
                                    rpe = actual.rpeList.getOrNull(i),
                                    durationSeconds = actual.durations.getOrNull(i),
                                    completed = true,
                                )
                            }.filter { it.reps > 0 || (it.durationSeconds ?: 0.0) > 0 }
                            com.peakform.fitness.core.state.StateModel.ExerciseRecord(
                                exerciseId = ex.id,
                                sets = setRecords,
                                skipped = false,
                            )
                        } else {
                            null
                        }
                    }.filterNotNull()

                    // Only write a session if at least one exercise has completed sets
                    if (sessionExercises.any { it.sets.isNotEmpty() }) {
                        val sessionRecord = com.peakform.fitness.core.state.StateModel.SessionRecord(
                            id = "session_${System.currentTimeMillis()}",
                            userId = ProState.activeProfileId,
                            timestamp = System.currentTimeMillis(),
                            exercises = sessionExercises,
                        )
                        ProState.appendSession(sessionRecord)
                    }
                    ProState.notifyChanged()
                    // C5: PR toast + C2 confetti(130) on a BIGGER day
                    val prHit = result.celebrationLines.any { it.contains("PR", ignoreCase = true) || it.contains("best", ignoreCase = true) } ||
                        result.celebrationTitle.contains("Perfect", ignoreCase = true)
                    if (prHit) {
                        prToast = "BIGGER day — new best logged!"
                        // reserve one session burst for the momentum dialog that follows
                        if (com.peakform.fitness.ui.Confetti.burstsThisSession >= 3) {
                            com.peakform.fitness.ui.Confetti.burstsThisSession = 2
                        }
                    }
                }
            },
            onDismiss = { completeDialog = false },
        )
    }

    // ---- celebration ----
    celebration?.let { result ->
        CelebrationDialog(result = result, onDismiss = {
            celebration = null
            showMomentum = true // C3: legacy "🎯 Next time, I want to be…" follows the summary
        })
    }

    // ---- momentum steering dialog (C3/C4) ----
    if (showMomentum) {
        com.peakform.fitness.ui.MomentumDialog(
            onBigger = { Sessions.applyMomentumTap("bigger"); showMomentum = false },
            onStronger = { Sessions.applyMomentumTap("stronger"); showMomentum = false },
            onDismiss = { showMomentum = false },
        )
    }

    // ---- share sheet ----
    if (showShare && workout != null) {
        com.peakform.fitness.ui.ShareSheet(workout = workout, onDismiss = { showShare = false })
    }

    // ---- PR toast ----
    prToast?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(2400); prToast = null }
        ToastBanner(msg)
    }

    // BATCH-38d: Grip equipment sheet
    if (showGripSheet) {
        GripEquipmentSheet(
            visible = true,
            onDismiss = { showGripSheet = false },
            onSelect = { selection ->
                com.peakform.fitness.core.ProfileSettings.put(ctx, "p8_grip_pref", selection)
                showGripSheet = false
                prToast = "Grip: $selection"
            },
        )
    }
}

@Composable
fun ExerciseCard(
    index: Int,
    exercise: WorkoutExercise,
    workoutId: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onLogged: () -> Unit,
    onAskSkip: () -> Unit,
    onSkip: (String) -> Unit,
    onRemove: () -> Unit,
    onStartRest: (Int) -> Unit,
    // BATCH-2: replace (confirm) + resume (re-open logger for a completed/skipped exercise)
    onAskReplace: () -> Unit = {},
    onResume: () -> Unit = {},
) {
    val c = LocalProColors.current
    val isDrill = exercise.noFatigue || exercise.isWarmup || exercise.isCooldown || exercise.isComponentDrill

    // BATCH-2: per-card UI toggles for superset / drop set / warmup (transient, not persisted —
    // matches the HTML's behavior where these are session-level display flags).
    var isSuperset by remember(exercise.id) { mutableStateOf(false) }
    var isDropset by remember(exercise.id) { mutableStateOf(false) }
    var isWarmupToggled by remember(exercise.id) { mutableStateOf(exercise.isWarmup) }
    var showInstructions by remember(exercise.id) { mutableStateOf(false) }

    // BATCH-35c B1+B2: resolve the coach ONCE at card top so requirements()
    // gates both the "Needs 1RM" chip / border state AND the OneRmTestPanel.
    // Before this fix, reqs was computed AFTER the state, so the panel and chip
    // rendered for Side Plank / Chin-Up / RDL — none of which declare needs1RM.
    val coach35c = com.peakform.fitness.coach.CoachRegistry.lookup(exercise.id)
    val reqs35c = coach35c?.requirements() ?: com.peakform.fitness.coach.authored.CoachRequirements()

    // BATCH-35e: difficulty gate. If the exercise is locked, the prescription
    // line is replaced with a lock icon + the gate's reason string. The card
    // itself stays in the list (not removed, not hidden).
    val unlock35e = remember(exercise.id, ProState.data.exercises.hashCode()) {
        com.peakform.fitness.core.DifficultyGate.isUnlocked(exercise.id, ProState.data)
    }

    // BATCH-37b/37c: dossier lookup. Read the per-exercise knowledge layer
    // (goals, landmarks, prerequisites, stimulus profiles) from DossierRegistry.
    val dossier = remember(exercise.id) {
        com.peakform.fitness.coach.dossiers.DossierRegistry.forExercise(exercise.id)
    }
    // BATCH-37c: three-tier label system:
    //   "cited"     — sommer/mcgill published source
    //   "first-pass" — peakform-2026/book.md with a convention landmark
    //   "awaiting"  — peakform-2026/book.md with empty landmark (unrecognized)
    val dossierTier = when {
        dossier == null -> "awaiting"
        dossier.provenance.source.startsWith("sommer-") ||
            dossier.provenance.source.startsWith("mcgill-") -> "cited"
        dossier.goals.firstOrNull()?.landmark?.isNotBlank() == true -> "first-pass"
        else -> "awaiting"
    }
    val isCited = dossierTier == "cited"

    // BATCH-37c: prerequisite gate. Prerequisites are authored as
    // informational sentences but the gate is NOT implemented — always open.
    // Future batches will implement actual checking when sentences become
    // machine-readable.
    val prereqsMet = true

    // card state
    val has1RM = (ProState.data.exercises[exercise.id]?.tested1RM ?: 0.0) > 0
    val state = when {
        exercise.skipped -> "skipped"
        exercise.actual != null -> "done"
        // B2: gate the needs1rm state on reqs.needs1RM so cards without an
        // authored coach (or coaches that don't declare needs1RM — Plank,
        // Pull-Up, BandedLR, Running) don't show the chip / amber border.
        !isDrill && !has1RM && reqs35c.needs1RM -> "needs1rm"
        else -> "ready"
    }
    val borderColor = when {
        // BATCH-38a: expanded card uses accent border (HTML .exercise-item.active)
        expanded -> c.accentLine
        state == "needs1rm" -> c.warn
        state == "done" -> c.ok
        // BATCH-38b: non-active border = tintLine (HTML --p4-tint-line)
        else -> c.tintLine
    }
    // BATCH-38a: 16dp radius matches HTML .exercise-item border-radius: 16px (was 18dp)
    val shape = RoundedCornerShape(16.dp)

    // BATCH-38a: card container uses Box to allow left accent bar overlay.
    // HTML .exercise-item::before is a 5px left bar with gradient, opacity 0 by default,
    // opacity 1 on .active. In Compose, we show it when expanded.
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            // BATCH-38b: card bg = tintBg (HTML .exercise-item background with --p4-tint)
            .background(c.tintBg)
            .border(2.dp, borderColor, shape),
    ) {
        // Left accent bar (HTML ::before) — 5dp wide, visible when expanded
        // BATCH-38b: gradient = accent → tintStrong (was accent → lighten)
        if (expanded) {
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(Brush.linearGradient(listOf(c.accent, c.tintStrong))),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(25.dp),  // BATCH-38a: 25dp matches HTML .exercise-item padding: 25px (was 16dp)
        ) {
        // header — the toggle target (FIX (audit): the whole card used to be clickable, so a
        // stray tap on labels/paddings collapsed the open logger and destroyed all input).
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onToggle() },
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(exercise.name, style = ProType.exerciseTitle, color = c.text, modifier = Modifier.weight(1f, fill = false))
                    Spacer(Modifier.width(8.dp))
                    if (state == "needs1rm") {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.warn.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("Needs 1RM", color = c.warn, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    } else if (state == "done") {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.ok.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("✓ Logged", color = c.ok, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    } else if (exercise.skipped) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.surface3).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("Skipped", color = c.text3, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    } else if (exercise.isWarmup) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.accentSoft).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("Warm-up", color = c.accent, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    } else if (exercise.isCooldown) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.accentSoft).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("Cooldown", color = c.accent, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                // FIX (audit): long muscle names could clip — scroll horizontally.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    exercise.muscleGroup.take(3).forEach { m ->
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.surface2).border(1.dp, c.hairline2, RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                            Text(m.replace('_', ' '), fontSize = 12.sp, color = c.text2)
                        }
                    }
                }
            }
            com.peakform.fitness.ui.FaIcon(if (expanded) "fa-chevron-up" else "fa-chevron-down", size = 14.sp, tint = c.accent)
        }

        // v34c Item 4: Card renders only declared fields per coach.requirements().
        // v34c Item 5: Component chips from authored rows.
        // v34d: Declaration sentence + [Coach, go] / [Change] buttons.
        // BATCH-38a: 20dp matches HTML .exercise-header margin-bottom: 20px (was 12dp)
        Spacer(Modifier.height(20.dp))
        val rx = exercise.prescribed
        val rec = ProState.data.exercises[exercise.id]

        // Re-use the top-of-card coach / requirements (B1+B2). The previous
        // duplicate lookup is gone; reqs here equals reqs35c above.
        val coach = coach35c
        val reqs = reqs35c
        val has1RM = (rec?.tested1RM ?: 0.0) > 0

        // B1: authored row lookup goes through the coach's exerciseIds so library
        // exercises whose id ("barbell_romanian_deadlift") doesn't match the
        // rowsForCoach key ("romanian_deadlift") still resolve. The first
        // exerciseId that has authored rows wins.
        val authoredRow = com.peakform.fitness.coach.authored.AuthoredRows.rowsForCoach(exercise.id).firstOrNull()
            ?: coach?.exerciseIds?.firstNotNullOfOrNull { id ->
                com.peakform.fitness.coach.authored.AuthoredRows.rowsForCoach(id).firstOrNull()
            }
        // BATCH-36a: component chips read from the taxonomy entry (not the
        // authored row's components map). The taxonomy has the canonical
        // 11-name enum (Strength, Hypertrophy, Endurance, …) at weight 1..3
        // derived from exercises.json's fitnessComponents. The authored row's
        // components map (uppercase keys like "STRENGTH", weight 1..5) is for
        // the prescription line / explain() output, not for chips.
        // Missing taxonomy → no chips rendered (composable returns early).
        val taxonomy = com.peakform.fitness.engine.Library.taxonomyFor(exercise.id)
        val taxonomyComponents = taxonomy?.components ?: emptyMap()
        if (taxonomyComponents.isNotEmpty()) {
            com.peakform.fitness.ui.components.ComponentChipsRow(taxonomyComponents)
            Spacer(Modifier.height(8.dp))
        }

        // BATCH-37c: dossier-derived UI — three-tier label system.
        //   cited → "Source: <domain>" + goal chip + landmark (accent)
        //   first-pass → "PeakForm standard — awaiting review" + landmark (text3)
        //   awaiting → "Awaiting human authoring." (text3, no landmark)
        when (dossierTier) {
            "cited" -> {
                val goal = dossier!!.goals.firstOrNull()
                if (goal != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.accentSoft).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text(goal.label, fontSize = 10.sp, color = c.accent, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Source: ${dossier.provenance.source.split("/")[0]}", fontSize = 10.sp, color = c.accent)
                    }
                    if (goal.landmark.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text("Landmark: ${goal.landmark}", style = ProType.small, color = c.accent)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
            "first-pass" -> {
                val goal = dossier!!.goals.firstOrNull()
                if (goal != null && goal.landmark.isNotBlank()) {
                    Text("PeakForm standard — awaiting review", style = ProType.small, color = c.text3)
                    Spacer(Modifier.height(2.dp))
                    Text("Landmark: ${goal.landmark}", style = ProType.small, color = c.text2)
                    Spacer(Modifier.height(6.dp))
                } else {
                    Text("PeakForm standard — awaiting review", style = ProType.small, color = c.text3)
                    Spacer(Modifier.height(6.dp))
                }
            }
            else -> {
                Text("Awaiting human authoring.", style = ProType.small, color = c.text3)
                Spacer(Modifier.height(6.dp))
            }
        }

        // v34d: Declaration sentence from coach.explain()
        val declarationText = if (authoredRow != null && coach != null) {
            // Build a prescription from the authored row to pass to explain()
            val pres = com.peakform.fitness.coach.authored.Prescription(
                sets = authoredRow.sets, reps = authoredRow.reps,
                pct1RM = authoredRow.pct1RM, bandColour = authoredRow.bandColour,
                tier = authoredRow.tier, duration = authoredRow.duration,
                distance = authoredRow.distance, pace = authoredRow.pace,
                restSeconds = authoredRow.restSeconds, components = authoredRow.components,
                sourceRowId = authoredRow.rowId,
            )
            val explained = coach.explain(pres, "STRONGER")
            if (explained.isNotBlank() && explained != pres.note) explained else ""
        } else ""
        if (declarationText.isNotBlank()) {
            Text(declarationText, style = ProType.body2.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), color = c.text2)
            Spacer(Modifier.height(6.dp))
        }

        // State for [Coach, go] button
        var coachAccepted by remember(exercise.id) { mutableStateOf(false) }
        // State for tune sheet
        var showTuneSheet by remember(exercise.id) { mutableStateOf(false) }

        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onToggle() }) {
            // BATCH-35e: if the exercise is locked, render a lock icon + the gate's
            // reason string in place of the prescription line. Card stays in the list.
            if (!unlock35e.unlocked) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.peakform.fitness.ui.FaIcon("fa-lock", size = 14.sp, tint = c.warn)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        unlock35e.reason,
                        style = ProType.body2,
                        color = c.warn,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
            } else if (!reqs.needs1RM && !reqs.needsBand && !reqs.needsBodyweight && !reqs.needsDuration && !reqs.needsDistance && !reqs.needsPace && !reqs.needsWeight) {
                // All-false requirements branch. This fires when there is no
                // authored coach (CoachRegistry.lookup returned null or a
                // generic with default empty requirements). Before BATCH-39c
                // the card always showed "No prescription available — no
                // coach requirements declared." even for exercises whose
                // DOSSIER exists with a PeakForm landmark. Now the
                // WorkoutCardMessages helper picks the right string based on
                // whether the dossier has a goal with a landmark or label.
                Text(
                    WorkoutCardMessages.noCoachMessage(dossier),
                    style = ProType.small,
                    color = if (WorkoutCardMessages.noCoachMessage(dossier) == WorkoutCardMessages.NO_COACH_DOSSIER_PRESENT) c.text2 else c.text3,
                )
            } else {
                // Render only fields the coach declares
                val parts = mutableListOf<String>()
                if (reqs.needsWeight) {
                    val w = rx.weight
                    if (w != null && w > 0) {
                        parts.add("${OneRm.roundToNearest(w).toInt()} lbs")
                        if (reqs.needs1RM && has1RM) {
                            val orm = rec?.tested1RM
                            if (orm != null && orm > 0) {
                                val pct = (w / orm * 100).toInt()
                                assertDeclared(reqs.needs1RM, "pct1RM", "1RM display")
                                parts.add("(${pct}% 1RM)")
                            }
                        }
                    }
                }
                if (reqs.needs1RM && !reqs.needsWeight) {
                    if (!has1RM) {
                        parts.add("Test 1RM first")
                    }
                }
                if (reqs.needsBand) {
                    val bandColour = com.peakform.fitness.core.ProfileSettings.get(com.peakform.fitness.ui.Fa.appContext!!, "p8_band_" + exercise.id)
                    parts.add(bandColour?.replaceFirstChar { it.uppercase() } ?: "Select band")
                }
                if (reqs.needsBodyweight) {
                    parts.add("BW")
                }
                if (reqs.needsDuration && !reqs.needsDistance) {
                    val dur = rx.duration ?: 45.0
                    parts.add("${dur.toInt()}s hold")
                }
                if (reqs.needsDistance) {
                    parts.add("${rx.weight?.toInt() ?: 0} mi")
                }
                if (reqs.needsPace) {
                    parts.add("Zone 2")
                }
                if (reqs.needsWeight || reqs.needsBand || reqs.needsBodyweight || reqs.needsDuration) {
                    parts.add("${rx.sets} × ${rx.reps}")
                }
                if (reqs.needsDistance) {
                    parts.remove("${rx.sets} × ${rx.reps}") // cardio doesn't show sets/reps
                }

                // Runtime assertion: no field renders whose requirement is false
                if (BuildConfig.DEBUG) {
                    if (!reqs.needs1RM && parts.any { it.contains("1RM") }) {
                        android.util.Log.e("ProCard", "undeclared field: 1RM in exercise ${exercise.id}")
                        throw IllegalStateException("undeclared field: 1RM in exercise ${exercise.id}")
                    }
                    if (!reqs.needsWeight && parts.any { it.contains("lbs") }) {
                        android.util.Log.e("ProCard", "undeclared field: load in exercise ${exercise.id}")
                        throw IllegalStateException("undeclared field: load in exercise ${exercise.id}")
                    }
                } else {
                    if (!reqs.needs1RM && parts.any { it.contains("1RM") }) android.util.Log.w("ProCard", "undeclared field: 1RM in exercise ${exercise.id}")
                    if (!reqs.needsWeight && parts.any { it.contains("lbs") }) android.util.Log.w("ProCard", "undeclared field: load in exercise ${exercise.id}")
                }

                RxLine(label = "PRESCRIPTION", value = parts.joinToString(" · "))
            }
        }

        // v34d: [Coach, go] and [Change] action buttons
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button(
                if (coachAccepted) "Accepted ✓" else "Coach, go",
                icon = if (coachAccepted) "fa-check" else "fa-play",
                style = if (coachAccepted) BtnStyle.SUCCESS else BtnStyle.PRIMARY,
                minHeight = 36,
                modifier = Modifier.weight(1f),
            ) {
                android.util.Log.d("ProCard", "Coach, go accepted for ${exercise.id}")
                coachAccepted = true
            }
            P4Button(
                "Change",
                icon = "fa-sliders",
                style = BtnStyle.SECONDARY,
                minHeight = 36,
                modifier = Modifier.weight(1f),
            ) {
                android.util.Log.d("ProCard", "Change requested for ${exercise.id} — opening tune sheet")
                showTuneSheet = true
            }
        }
        if (showTuneSheet) {
            // v34e: Open tune sheet with pre-populated prescription.
            val tunePres = if (authoredRow != null) {
                com.peakform.fitness.coach.authored.Prescription(
                    sets = authoredRow.sets, reps = authoredRow.reps,
                    pct1RM = authoredRow.pct1RM, bandColour = authoredRow.bandColour,
                    tier = authoredRow.tier, duration = authoredRow.duration,
                    distance = authoredRow.distance, pace = authoredRow.pace,
                    restSeconds = authoredRow.restSeconds, components = authoredRow.components,
                    sourceRowId = authoredRow.rowId,
                )
            } else null
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTuneSheet = false },
                confirmButton = {},
                text = {
                    com.peakform.fitness.ui.screens.TuningSheet(
                        visible = true,
                        initialPrescription = tunePres,
                        onDismiss = { showTuneSheet = false },
                        onSave = { saved ->
                            android.util.Log.d("ProCard", "Tune saved for ${exercise.id}: $saved")
                        },
                    )
                },
            )
        }

        // BATCH-2: 'How to do' + 'Images' buttons — always visible (open system browser)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("How to do", icon = "fa-magnifying-glass", style = BtnStyle.GHOST, minHeight = 32, modifier = Modifier.weight(1f)) {
                openExerciseBrowser(exercise.name + " how to do", images = false)
            }
            P4Button("Images", icon = "fa-image", style = BtnStyle.GHOST, minHeight = 32, modifier = Modifier.weight(1f)) {
                openExerciseBrowser(exercise.name + " exercise", images = true)
            }
        }

        // BATCH-2: plate math — shown on each working set when a barbell weight is prescribed
        // (e.g., "45 + 45 + 25" per side for 205 lbs with a 45-lb bar).
        val prescribedW = rx.weight
        if (exercise.equipment.equals("barbell", ignoreCase = true) && prescribedW != null && prescribedW > 45.0) {
            Spacer(Modifier.height(6.dp))
            val plates = plateMath(prescribedW)
            if (plates.isNotEmpty()) {
                Text(
                    "Plate math: ${plates.joinToString(" + ")} lbs/side (×2 + 45 bar = ${prescribedW.toInt()} lbs)",
                    style = ProType.small, color = c.accent,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(12.dp))
                if (exercise.progressionNotes.isNotBlank()) {
                    Text(exercise.progressionNotes, style = ProType.small, color = c.text3)
                    Spacer(Modifier.height(10.dp))
                }

                // BATCH-2: superset / drop set / warmup toggles (3 chips, mutually compatible)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PlateToggleChip("Superset", active = isSuperset, tint = c.accent) { isSuperset = !isSuperset; if (isSuperset) isDropset = false }
                    PlateToggleChip("Drop set", active = isDropset, tint = c.warn) { isDropset = !isDropset; if (isDropset) isSuperset = false }
                    PlateToggleChip("Warmup", active = isWarmupToggled, tint = c.ok) { isWarmupToggled = !isWarmupToggled }
                }
                Spacer(Modifier.height(10.dp))

                // BATCH-2: 'Show Instructions' toggle — expands a numbered step list
                if (exercise.instructions.isNotEmpty()) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { showInstructions = !showInstructions }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        com.peakform.fitness.ui.FaIcon(if (showInstructions) "fa-chevron-down" else "fa-chevron-right", size = 12.sp, tint = c.accent)
                        Spacer(Modifier.width(8.dp))
                        Text(if (showInstructions) "Hide instructions" else "Show instructions (${exercise.instructions.size} steps)", style = ProType.body2, color = c.accent)
                    }
                    AnimatedVisibility(visible = showInstructions) {
                        Column(Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            exercise.instructions.forEachIndexed { i, step ->
                                Text("${i + 1}. $step", style = ProType.body2, color = c.text2)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (exercise.skipped) {
                    Text("Skipped — ${exercise.actual?.notes ?: ""}", style = ProType.body2, color = c.text3)
                } else if (exercise.actual != null) {
                    val a = exercise.actual!!
                    Text(
                        buildString {
                            append("Logged: ${a.sets} sets")
                            if (a.reps.isNotEmpty()) append(" × ${a.reps.joinToString(", ") { com.peakform.fitness.core.ProJson.formatNum(it) }} reps")
                            if (a.weight > 0) append(" @ ${a.weight.toInt()} lbs")
                            if (a.volume != null) append(" · ${com.peakform.fitness.engine.Stats.formatNumber(a.volume!!)} lbs volume")
                        },
                        style = ProType.body2, color = c.ok,
                    )
                } else {
                    // B2: gate the 1RM test panel on reqs.needs1RM (same gate the
                    // prescription line uses). Side Plank, Pull-Up, RDL no longer show it.
                    if (!isDrill && !has1RM && reqs.needs1RM) {
                        OneRmTestPanel(
                            exerciseId = exercise.id,
                            onDone = { onLogged() },
                            onSkip = { onAskSkip() },
                            onStartRest = onStartRest,
                        )
                    } else {
                        LoggerPanel(
                            exercise = exercise,
                            onSave = { actual, notes ->
                                val w = ProState.currentWorkout ?: return@LoggerPanel
                                val result = Sessions.applyPerformance(w, index, actual, notes)
                                if (result.ok && result.workout != null) {
                                    ProState.currentWorkout = result.workout
                                    ProState.performSave()
                                }
                                onLogged()
                            },
                            onStartRest = onStartRest,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        P4Button("Skip", onClick = onAskSkip, style = BtnStyle.GHOST, minHeight = 40, modifier = Modifier.weight(1f))
                        if (!exercise.isWarmup && !exercise.isCooldown) {
                            P4Button("Replace", onClick = onAskReplace, style = BtnStyle.GHOST, minHeight = 40, modifier = Modifier.weight(1f))
                            P4Button("Remove", onClick = onRemove, style = BtnStyle.GHOST, minHeight = 40, modifier = Modifier.weight(1f))
                        }
                    }
                }
                // BATCH-2: Resume action — re-opens the logger for a completed/skipped exercise
                if (exercise.isLogged) {
                    Spacer(Modifier.height(8.dp))
                    P4Button("Resume this exercise", icon = "fa-rotate-left", style = BtnStyle.INFO, minHeight = 38, modifier = Modifier.fillMaxWidth()) {
                        onResume()
                    }
                }
            }
        }
    }
    }  // BATCH-38a: close Box wrapper (card container with left accent bar)
}

/** Reads a boolean from the p4_power_settings JSON (default when absent). */
internal fun powerFlag(key: String, default: Boolean): Boolean {
    val ctx = com.peakform.fitness.ui.Fa.appContext ?: return default
    return try {
        val raw = com.peakform.fitness.core.ProPrefs.get(ctx, "p4_power_settings") ?: return default
        val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as kotlinx.serialization.json.JsonObject
        val p = (o[key] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: return default
        p != "false"
    } catch (_: Exception) {
        default
    }
}

/** Reads a notification preference (p4_notif_<key>, absent = on) — FIX (audit): the
 * Settings notification toggles persisted but nothing read them; the retest banner and
 * the Deload chip are now wired to their switches. */
internal fun notifFlag(key: String, default: Boolean = true): Boolean {
    val ctx = com.peakform.fitness.ui.Fa.appContext ?: return default
    return com.peakform.fitness.core.ProPrefs.get(ctx, "p4_notif_$key") != "false"
}

@Composable
fun RxLine(label: String, value: String) {
    val c = LocalProColors.current
    // BATCH-38a: matches HTML .exercise-prescription styling:
    //   background: linear-gradient(135deg, rgba(16,185,129,0.05), rgba(59,130,246,0.05))
    //   padding: 20px
    //   border-radius: 12px
    //   border-left: 4px solid var(--success) (= c.ok)
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))  // HTML: 12px (was 14dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        c.ok.copy(alpha = 0.05f),      // HTML: rgba(16,185,129,0.05)
                        c.accent.copy(alpha = 0.05f),  // HTML: rgba(59,130,246,0.05)
                    )
                )
            )
            .border(1.dp, c.hairline2, RoundedCornerShape(12.dp)),
    ) {
        // Left accent: 4dp solid c.ok (HTML: border-left: 4px solid var(--success))
        Box(Modifier.width(4.dp).fillMaxHeight().background(c.ok))
        Column(
            Modifier.weight(1f).padding(20.dp),  // HTML: 20px padding (was 14/10dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, letterSpacing = 0.8.sp, color = c.accent)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, color = c.accent, fontFamily = SpaceGrotesk)
        }
    }
}

// ---- BATCH-2 helpers ----

/** Opens the system browser with a Google search for the exercise (How-to or Images tab). */
private fun openExerciseBrowser(query: String, images: Boolean) {
    val ctx = com.peakform.fitness.ui.Fa.appContext ?: return
    val url = "https://www.google.com/search?q=" + android.net.Uri.encode(query) + if (images) "&tbm=isch" else ""
    try {
        ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: Exception) { }
}

/**
 * Plate math — computes the plate denominations per side for a barbell load.
 * Standard 45-lb Olympic bar, plates in 45/35/25/10/5/2.5 lb denominations.
 * Returns the per-side plate list (e.g., 205 lbs → [45, 25, 10] = 80 lbs/side × 2 + 45 bar = 205).
 */
fun plateMath(totalLbs: Double, barWeight: Double = 45.0): List<Int> {
    if (totalLbs <= barWeight) return emptyList()
    val perSide = (totalLbs - barWeight) / 2.0
    val denominations = listOf(45, 35, 25, 10, 5, 2)
    val plates = mutableListOf<Int>()
    var remaining = perSide
    for (d in denominations) {
        while (remaining >= d - 0.01) {
            plates.add(d)
            remaining -= d
        }
    }
    return plates
}

/** A small toggle chip used for superset / drop set / warmup flags on exercise cards. */
@Composable
fun PlateToggleChip(label: String, active: Boolean, tint: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val c = LocalProColors.current
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) tint.copy(alpha = 0.18f) else c.surface2)
            .border(1.dp, if (active) tint else c.hairline2, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(label, fontSize = 11.sp, color = if (active) tint else c.text2, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
    }
}

// BATCH-36a: the per-card ComponentChipsRow has moved to
// com.peakform.fitness.ui.components.ComponentChipsRow (in Kit.kt) so it can
// be shared between the Workout card and the Library card without duplication.
// The signature gained a maxShown: Int = 4 parameter; the implementation
// matches the HTML .p4-chip + .p4-chiprow design (999px radius, 8dp gap,
// 5-segment weight bar with scaled accent alpha).

/** Runtime assertion helper — no field renders whose requirement is false. */
private fun assertDeclared(requirement: Boolean, fieldName: String, context: String) {
    if (!requirement) {
        if (com.peakform.fitness.BuildConfig.DEBUG) {
            android.util.Log.e("ProCard", "undeclared field: $fieldName in $context")
            throw IllegalStateException("undeclared field: $fieldName in $context")
        } else {
            android.util.Log.w("ProCard", "undeclared field: $fieldName in $context")
        }
    }
}

/**
 * BATCH-38c/38d: Floating action rail — 5 vertical rounded-square buttons on the
 * right side of the workout screen.
 *
 * Buttons (top to bottom):
 *   1. Timer / stopwatch — opens a rest timer
 *   2. Cloud download — backup (wired in v38d to Backup.exportCompleteV1 + file picker)
 *   3. Cloud upload — import (wired to onOpenSection("import"))
 *   4. Hand / grip — opens GripEquipmentSheet (wired in v38d)
 *   5. Arrow up — scroll to top
 */
@Composable
fun FloatingActionRail(
    onTimer: () -> Unit,
    onDownload: () -> Unit,
    onUpload: () -> Unit,
    onGrip: () -> Unit,
    onScrollTop: () -> Unit,
) {
    val c = LocalProColors.current
    val buttons = listOf(
        Triple("fa-stopwatch", "Timer", onTimer),
        Triple("fa-cloud-arrow-down", "Backup", onDownload),
        Triple("fa-cloud-arrow-up", "Import", onUpload),
        Triple("fa-hand", "Grip", onGrip),
        Triple("fa-arrow-up", "Top", onScrollTop),
    )
    Box(
        Modifier
            .fillMaxSize()
            .padding(end = 8.dp, bottom = 80.dp),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            buttons.forEach { (icon, label, onClick) ->
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(c.tintBg)
                        .border(1.dp, c.tintLine, RoundedCornerShape(14.dp))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    com.peakform.fitness.ui.FaIcon(icon, size = 16.sp, tint = c.accent)
                }
            }
        }
    }
}

/**
 * BATCH-38d: Grip equipment selector sheet.
 *
 * A modal sheet (GlassDialogHost pattern from Dialogs.kt) showing a grid of
 * equipment values (from exercise_taxonomy.json) + grip-specific items found
 * in the library's exercise names.
 *
 * On select: stores the selection via ProfileSettings.put(ctx, "p8_grip_pref", selection).
 * The selection biases nothing in this batch — it is a preference for future use.
 */
@Composable
fun GripEquipmentSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current

    // Equipment values from exercise_taxonomy.json (8 allowed)
    val equipmentValues = listOf(
        "barbell", "dumbbell", "machine", "cable",
        "band", "bodyweight", "kettlebell", "other",
    )
    // Grip-specific values found in the library's exercise names
    val gripValues = listOf(
        "Wide Grip", "Close Grip", "Reverse Grip", "Neutral Grip",
        "Snatch Grip", "Clean Grip", "Pinch Grip",
    )

    com.peakform.fitness.ui.GlassDialogHost(
        onDismiss = onDismiss,
        scrollable = true,
    ) {
        Text("Equipment", style = ProType.label, color = c.text)
        Spacer(Modifier.height(8.dp))
        // Equipment grid — 4 columns, tinted backgrounds
        equipmentValues.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { equip ->
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(c.tintBg)
                            .border(1.dp, c.tintLine, RoundedCornerShape(10.dp))
                            .clickable { onSelect(equip) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(equip.replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = c.text2, maxLines = 1)
                    }
                }
                // Pad with empty boxes if row is short
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("Grip Type", style = ProType.label, color = c.text)
        Spacer(Modifier.height(8.dp))
        gripValues.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { grip ->
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(c.tintBg)
                            .border(1.dp, c.tintLine, RoundedCornerShape(10.dp))
                            .clickable { onSelect(grip) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(grip, fontSize = 11.sp, color = c.text2, maxLines = 1)
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
