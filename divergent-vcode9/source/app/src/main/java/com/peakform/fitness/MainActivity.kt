package com.peakform.fitness

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.engine.Library
import com.peakform.fitness.core.MigrationBridge
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.Badges
import com.peakform.fitness.core.HealthScreen
import com.peakform.fitness.core.Motivation
import com.peakform.fitness.core.Notify
import com.peakform.fitness.core.ProfileRegistry
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProfileSwitcher
import com.peakform.fitness.core.StreakFire
import com.peakform.fitness.core.Synonyms
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.ui.components.BtnStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.DisposableEffect
import com.peakform.fitness.engine.Generator
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.LuxuryStartButton
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.SectionTitle
import com.peakform.fitness.ui.extras.BadgesScreen
import com.peakform.fitness.core.ProWidgetProvider
import com.peakform.fitness.ui.extras.CycleScreen
import com.peakform.fitness.ui.extras.ThemePickerScreen
import com.peakform.fitness.ui.extras.ConfettiOverlay
import com.peakform.fitness.ui.extras.GlossaryScreen
import com.peakform.fitness.ui.extras.HealthScreenFlow
import com.peakform.fitness.ui.extras.ImportWarningDialog
import com.peakform.fitness.ui.extras.LegalCenterScreen
import com.peakform.fitness.ui.extras.MuscleWikiScreen
import com.peakform.fitness.ui.extras.OnboardingWizard
import com.peakform.fitness.ui.extras.ProfilesScreen
import com.peakform.fitness.ui.extras.QuickActionsScreen
import com.peakform.fitness.ui.extras.ShareScreen
import com.peakform.fitness.ui.extras.SnapshotsScreen
import com.peakform.fitness.ui.extras.StreakRecoveryDialog
import com.peakform.fitness.ui.extras.StudioScreen
import com.peakform.fitness.ui.screens.*
import kotlinx.coroutines.launch

/**
 * MainActivity — 100% native shell. No WebView UI anywhere; the only WebView is the
 * invisible one-time migration shuttle (MigrationBridge).
 *
 * v1.3.0: onboarding gate, health-screen gating of generation, real destinations for
 * every More-sheet cell, streak-fire header, confetti on completion, share-to-app,
 * notification scheduling, widget refresh.
 */
class MainActivity : ComponentActivity() {
    var incomingShareText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Fa.appContext = applicationContext
        ProState.init(this)
        // FIX (audit): EngineHooks.rebuildDerived was never assigned — after imports the
        // derived aggregates/fatigue were not rebuilt until some other engine call happened.
        com.peakform.fitness.core.EngineHooks.rebuildDerived = {
            com.peakform.fitness.engine.Fatigue.rebuildFromHistory()
            com.peakform.fitness.engine.Fatigue.calculateMuscleLastTrained()
            com.peakform.fitness.engine.Sessions.recomputeAggregates()
        }
        ThemeController.load(ProPrefs.get(this, "p4_theme"))
        Notify.ensureChannels(this)
        Notify.scheduleDaily(this)
        if (intent?.action == Intent.ACTION_SEND) {
            incomingShareText = intent.getStringExtra(Intent.EXTRA_TEXT)
        }

        setContent {
            val scope = rememberCoroutineScope()
            val ctx = LocalContext.current

            // boot: hydrate data, then run migration if needed
            var booted by remember { mutableStateOf(false) }
            var migrationPhase by remember { mutableStateOf<String?>(null) }
            var migrationInfo by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(Unit) {
                ProState.loadAll()
                Library.ensure(ctx)
                Vocab.ensure(ctx)
                Synonyms.ensure(ctx)
                Badges.ensure(ctx)
                Motivation.ensure(ctx)
                ProfileSwitcher.hydrateActive(ctx)
                Badges.recompute(ctx)
                booted = true
                ProWidgetProvider.refresh(ctx)
                if (!MigrationBridge.isDone(ctx) && !MigrationBridge.running) {
                    migrationPhase = "checking"
                    val found = MigrationBridge.run(ctx) { p ->
                        migrationPhase = p.phase
                        migrationInfo = when {
                            p.workouts > 0 -> "${p.workouts} workouts · ${p.exercises} exercise records"
                            p.profile.isNotBlank() -> p.profile
                            else -> null
                        }
                    }
                    ProLog.i("MIGRATE", "migration finished: $found")
                    migrationPhase = if (found) "complete" else "skipped"
                    kotlinx.coroutines.delay(1800)
                    migrationPhase = null
                    ProState.notifyChanged()
                }
            }

            ProTheme(dark = ThemeController.dark, accent = ThemeController.accent()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(LocalProColors.current.bg, LocalProColors.current.bg)
                            )
                        )
                ) {
                    if (!booted) {
                        BootSplash()
                    } else {
                        AppShell(
                            migrationPhase = migrationPhase,
                            migrationInfo = migrationInfo,
                            shareText = incomingShareText,
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        ProState.flush()
    }
}

@Composable
fun BootSplash() {
    val c = LocalProColors.current
    Box(Modifier.fillMaxSize().background(c.bg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.peakform.fitness.ui.FaIcon("fa-dumbbell", size = 26.sp, tint = c.accent)
                Spacer(Modifier.width(10.dp))
                Text("Pro", style = ProType.brand, color = c.text)
            }
            Spacer(Modifier.height(12.dp))
            Text("Loading your training data…", style = ProType.body2, color = c.text3)
        }
    }
}

@Composable
fun AppShell(
    migrationPhase: String?,
    migrationInfo: String?,
    shareText: String? = null,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var section by remember { mutableStateOf("dashboard") }
    var sheetOpen by remember { mutableStateOf(false) }
    var timeWizard by remember { mutableStateOf(false) }
    var genLongevity by remember { mutableStateOf(false) }
    var wikiMuscle by remember { mutableStateOf<String?>(null) }
    var importRaw by remember { mutableStateOf<String?>(null) }
    var showConfetti by remember { mutableStateOf(false) }
    var showStreakRecovery by remember { mutableStateOf(false) }
    val snack = remember { mutableStateOf<String?>(null) }
    val shellScope = rememberCoroutineScope()

    // FIX (audit): the shell must re-compose when data changes (draft badge, hasDraft wizard step).
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        ProState.listeners.add(l)
        onDispose { ProState.listeners.remove(l) }
    }

    // onboarding gate — suppressed whenever user data exists (legacy hasAnyUserData)
    val needsOnboarding = remember(ver.intValue) {
        ProPrefs.get(ctx, "p4_onboarded") == null &&
            ProState.data.workouts.isEmpty() &&
            ProState.data.exercises.isEmpty() &&
            ProState.data.user.name.isBlank()
    }

    // confetti trigger: a workout just completed (workout count increased with a fresh dateCompleted)
    var lastDoneCount by remember { mutableStateOf(-1) }
    LaunchedEffect(ver.intValue) {
        val doneCount = ProState.data.workouts.count { it.isCompleted }
        if (lastDoneCount >= 0 && doneCount > lastDoneCount) {
            showConfetti = true
            Badges.recompute(ctx)
            ProWidgetProvider.refresh(ctx)
        }
        lastDoneCount = doneCount
    }

    // share-to-app: text received from another app
    LaunchedEffect(shareText) {
        if (!shareText.isNullOrBlank()) {
            importRaw = shareText
        }
    }

    val hasDraft = remember(ver.intValue) {
        ProState.currentWorkout != null && ProState.currentWorkout!!.exercises.any { !it.isLogged }
    }

    /** Generate with an optional time budget, then land the user on the workout screen (legacy showSection('workout')). */
    fun generateAndGo(budget: Int?) {
        // Health gating: tier C without recorded clearance pauses generation (legacy lock).
        if (HealthScreen.isLocked(ctx)) {
            snack.value = HealthScreen.LOCK_TOAST
            section = "health"
            return
        }
        shellScope.launch {
            Generator.performGenerateWorkout(budget)
            section = "workout"
            ProWidgetProvider.refresh(ctx)
        }
    }

    fun go(target: String) {
        sheetOpen = false
        section = when (target) {
            "resume" -> "workout"
            "export", "backup" -> "settings"
            // v1.3.0: every More-sheet cell is a real destination now
            "themes", "glossary", "studio", "period" -> target
            "long" -> "recovery"
            else -> target
        }
        if (target == "themes") { /* themes screen */ }
        if (target == "period") { /* cycle screen routes below */ }
    }

    // FIX (audit): the Longevity Workout was a dead end — the flag was written but never read.
    LaunchedEffect(genLongevity) {
        if (genLongevity) {
            genLongevity = false
            Generator.generateLongevityWorkout()
            section = "workout"
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            // ---- top bar (56dp compact navbar) ----
            TopBar(
                onAvatarTap = { section = "settings" },
                onFireTap = {
                    val streak = Stats.calculateStreak()
                    val daysSince = StreakFire.daysSinceLast(ProState.data, System.currentTimeMillis())
                    if (streak == 0 && ProState.data.workouts.isNotEmpty()) showStreakRecovery = true
                    else section = "dashboard"
                },
                onResumeTap = { section = "workout" },
            )

            // ---- migration banner ----
            AnimatedVisibility(visible = migrationPhase != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.accentSoft)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.peakform.fitness.ui.FaIcon("fa-cloud-arrow-down", size = 13.sp, tint = c.accent)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (migrationPhase) {
                            "checking" -> "Checking for data from the old app…"
                            "reading" -> "Importing legacy data${if (migrationInfo.isNullOrBlank()) "" else " — $migrationInfo"}…"
                            "importing" -> "Merging legacy data…"
                            "complete" -> "Legacy data imported ✓${if (migrationInfo.isNullOrBlank()) "" else " — $migrationInfo"}"
                            "skipped" -> "No legacy data found — starting fresh"
                            else -> ""
                        },
                        style = ProType.small, color = c.accent,
                    )
                }
            }

            // ---- screen content ----
            Box(Modifier.weight(1f)) {
                val scroll = rememberScrollState()
                Crossfade(targetState = section, animationSpec = androidx.compose.animation.core.tween(180), label = "screens") { s ->
                    when (s) {
                        "dashboard" -> DashboardScreen(
                            onStartWorkout = {
                                if (HealthScreen.isLocked(ctx)) {
                                    snack.value = HealthScreen.LOCK_TOAST
                                    section = "health"
                                } else {
                                    timeWizard = true
                                }
                            },
                            onResumeWorkout = { section = "workout" },
                            onOpenSection = { go(it) },
                        )
                        "workout" -> WorkoutScreen(
                            onRequireGenerate = { generateAndGo(null) },
                            onOpenSection = { go(it) },
                        )
                        "history" -> HistoryScreen(onOpenSection = { go(it) })
                        "progress" -> ProgressScreen(onOpenSection = { go(it) })
                        "recovery" -> RecoveryScreen(
                            onOpenSection = { go(it) },
                            onRequireGenerate = { kind ->
                                if (kind == "longevity") genLongevity = true else generateAndGo(null)
                            },
                        )
                        "library" -> LibraryScreen(onAddToWorkout = { ex ->
                            val cw = ProState.currentWorkout
                            if (cw == null) {
                                snack.value = "Generate a workout first, then add exercises."
                            } else {
                                // FIX (audit): weight = ex.defaultDuration assigned SECONDS as lbs
                                // ("plank @ 480 lbs"). Use the engine's next recommended weight,
                                // and keep duration where it belongs.
                                val rec = ProState.data.exercises[ex.id]
                                val isTime = ex.prescriptionType == "time"
                                val newEx = com.peakform.fitness.core.WorkoutExercise(
                                    id = ex.id, name = ex.name, muscleGroup = ex.muscles,
                                    prescribed = com.peakform.fitness.core.Prescription(
                                        sets = ex.defaultSets, reps = ex.defaultReps,
                                        weight = if (isTime) null else rec?.nextWeight,
                                        duration = if (isTime) ex.defaultDuration else null,
                                    ),
                                    progressionNotes = ex.progression, equipment = ex.equipment,
                                    instructions = ex.instructions, prescriptionType = ex.prescriptionType,
                                    loadDistribution = ex.muscles.associateWith { 1.0 / ex.muscles.size },
                                    noFatigue = ex.noFatigue, fitnessComponents = ex.fitnessComponents,
                                    defaultDuration = ex.defaultDuration, strengthIndex = ex.strengthIndex,
                                )
                                ProState.currentWorkout = cw.copy(exercises = cw.exercises + newEx)
                                ProState.performSave()
                                ProState.notifyChanged()
                                snack.value = "${ex.name} added to today's workout"
                                section = "workout"
                            }
                        }, onOpenWiki = { m ->
                            wikiMuscle = m
                            section = "wiki"
                        }, onToast = { snack.value = it })
                        "settings" -> SettingsScreen(onOpenSection = { go(it) })
                        // ---- v1.3.0 parity screens ----
                        "onboarding" -> OnboardingWizard(
                            onFinished = { section = "dashboard" },
                            onOpenLegal = { doc -> wikiMuscle = null; section = "legal"; },
                        )
                        "health" -> HealthScreenFlow(onDone = { section = "dashboard" }, onOpenLegal = { section = "legal" })
                        "legal" -> LegalCenterScreen(initialDoc = null, onBack = { section = "settings" })
                        "themes" -> ThemePickerScreen(onBack = { section = "settings" })
                        "glossary" -> GlossaryScreen(onBack = { section = "settings" })
                        "studio" -> StudioScreen(onBack = { section = "settings" }, onToast = { snack.value = it })
                        "snapshots" -> SnapshotsScreen(onBack = { section = "settings" }, onToast = { snack.value = it })
                        "profiles" -> ProfilesScreen(onBack = { section = "settings" }, onToast = { snack.value = it })
                        "badges" -> BadgesScreen(onBack = { section = "settings" })
                        "share" -> ShareScreen(onBack = { section = "workout" })
                        "quick" -> QuickActionsScreen(onBack = { section = "dashboard" }, onAction = { go(it) })
                        "wiki" -> MuscleWikiScreen(muscle = wikiMuscle, onBack = { wikiMuscle = null; section = "library" })
                        "period" -> CycleScreen(onBack = { section = "settings" })
                    }
                }
            }

            // ---- dock (outside scroll content — cannot disappear) ----
            Box(Modifier.fillMaxWidth().padding(bottom = 8.dp).navigationBarsPadding()) {
                GlassDock(
                    activeTab = if (section == "more") "dashboard" else section,
                    hasDraft = hasDraft,
                    onSelect = { id ->
                        if (id == "more") sheetOpen = true else go(id)
                    },
                )
            }
        }

        // More sheet
        MoreSheet(
            visible = sheetOpen,
            onDismiss = { sheetOpen = false },
            onSelect = { go(it) },
            quickChips = {
                P4Button(if (ThemeController.dark) "Light" else "Dark", onClick = {
                    val newMode = if (ThemeController.dark) "light" else "dark"
                    ThemeController.set(newMode, ThemeController.accentId)
                    ProPrefs.put(ctx, "p4_theme", """{"mode":"$newMode","accent":"${ThemeController.accentId}"}""")
                }, icon = if (ThemeController.dark) "fa-sun" else "fa-moon", style = BtnStyle.SECONDARY, minHeight = 36)
                P4Button("Export", onClick = { go("settings") }, icon = "fa-file-export", style = BtnStyle.SECONDARY, minHeight = 36)
                P4Button("Resume", onClick = { go("workout") }, icon = "fa-play", style = BtnStyle.SECONDARY, minHeight = 36)
            },
        )

        // time wizard
        if (timeWizard) {
            TimeWizardDialog(
                hasDraft = hasDraft,
                onDismiss = { timeWizard = false },
                onContinue = { timeWizard = false; section = "workout" },
                onGenerate = { budget ->
                    timeWizard = false
                    generateAndGo(budget)
                },
            )
        }

        // streak recovery (honor system)
        if (showStreakRecovery) {
            StreakRecoveryDialog(
                onDismiss = { showStreakRecovery = false },
                onRecovered = { msg ->
                    snack.value = msg
                    ProState.notifyChanged()
                },
            )
        }

        // share-to-app import (JSON from another app) → import warning + healing
        importRaw?.let { raw ->
            ImportWarningDialog(
                raw = raw,
                onConfirm = {
                    val healed = com.peakform.fitness.core.Heal.heal(raw)
                    importRaw = null
                    shellScope.launch {
                        val result = com.peakform.fitness.core.Backup.import(ctx, healed)
                        ProState.notifyChanged()
                        snack.value = result.message
                    }
                },
                onDismiss = { importRaw = null },
            )
        }

        // confetti overlay
        if (showConfetti) {
            ConfettiOverlay(onDone = { showConfetti = false })
        }

        // snack
        snack.value?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(2200)
                snack.value = null
            }
            Box(Modifier.fillMaxWidth().padding(bottom = 120.dp), contentAlignment = Alignment.BottomCenter) {
                com.peakform.fitness.ui.screens.ToastBanner(msg)
            }
        }
    }
}

/** 56dp compact glass navbar — v1.3.0: streak fire w/ intensity tiers, emoji avatar, resume pill. */
@Composable
fun TopBar(onAvatarTap: () -> Unit, onFireTap: () -> Unit = {}, onResumeTap: () -> Unit = {}) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    // FIX (audit): streak/name were remember{} — frozen forever. Re-read on every ProState change.
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        ProState.listeners.add(l)
        onDispose { ProState.listeners.remove(l) }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(c.glass)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        com.peakform.fitness.ui.FaIcon("fa-dumbbell", size = 17.sp, tint = c.accent)
        Spacer(Modifier.width(8.dp))
        Text("Pro", style = ProType.brand, fontSize = 19.sp, color = c.text)
        Spacer(Modifier.width(10.dp))

        // streak fire with intensity tiers (legacy updateStreakFire)
        val streak = remember(ver.intValue) { Stats.calculateStreak() }
        val daysSince = remember(ver.intValue) { StreakFire.daysSinceLast(ProState.data, System.currentTimeMillis()) }
        val fire = remember(ver.intValue) { StreakFire.of(streak, daysSince) }
        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onFireTap() }
                .padding(horizontal = 5.dp, vertical = 3.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(fire.glyph, fontSize = 13.sp, modifier = Modifier.alpha(fire.opacity))
                Spacer(Modifier.width(3.dp))
                Text("${com.peakform.fitness.ui.components.rememberCountUp(streak)}", style = ProType.label, color = c.text)
            }
        }

        // Resume Workout pill (legacy header button)
        val hasDraftNow = ProState.currentWorkout != null && ProState.currentWorkout!!.exercises.any { !it.isLogged }
        if (hasDraftNow) {
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(c.accentSoft)
                    .border(1.dp, c.accent, RoundedCornerShape(999.dp))
                    .clickable { onResumeTap() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaIcon("fa-play", size = 9.sp, tint = c.accent)
                    Spacer(Modifier.width(4.dp))
                    Text("Resume", style = ProType.small, color = c.accent)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        val user = ProState.data.user
        val name = user.name.trim().split(Regex("\\s+")).firstOrNull().takeUnless { it.isNullOrEmpty() } ?: "Athlete"
        val exp = user.experience.replaceFirstChar { it.uppercase() }.ifBlank { "Beginner" }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.clickable { onAvatarTap() }) {
            // Luxury header name — legacy .user-name (2891, 2909-2928): serif fallback, muted
            // gold gradient text-clip 135deg #e2c28b→#a67c1e, uppercase, letter-spaced.
            // v1.4.0 native extra: slow highlight sweep (UI expert technique 3).
            val goldT = rememberInfiniteTransition("gold-name")
            val gx by goldT.animateFloat(
                -0.6f, 1.6f,
                infiniteRepeatable(tween(2600, easing = LinearEasing)),
                label = "gx",
            )
            val goldBrush = Brush.linearGradient(
                listOf(Color(0xFFE2C28B), Color(0xFFC9A03D), Color(0xFFF7E8C6), Color(0xFFC9A03D), Color(0xFFE2C28B)),
                start = Offset(gx * 260f - 80f, 0f),
                end = Offset(gx * 260f + 60f, 34f),
            )
            Text(
                name.uppercase(),
                style = ProType.small.copy(
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.2.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    brush = goldBrush,
                ),
                fontSize = 12.sp,
            )
            Text("$exp • $streak Day Streak", fontSize = 9.sp, color = c.text3)
        }
        Spacer(Modifier.width(10.dp))
        // emoji avatar (legacy P4.Emoji animal picker); falls back to initial
        val emoji = ProPrefs.get(ctx, "p4_avatar")
        Box(
            Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(c.accentGradient)
                .clickable { onAvatarTap() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                emoji?.takeIf { it.isNotBlank() } ?: name.take(1).uppercase(),
                color = if (emoji.isNullOrBlank()) c.onAccent else Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = if (emoji.isNullOrBlank()) 16.sp else 18.sp,
            )
        }
    }
}

/** Start-flow wizard: Continue-or-Replace + time budget (FIX #15, 1:1). */
@Composable
fun TimeWizardDialog(hasDraft: Boolean, onDismiss: () -> Unit, onContinue: () -> Unit, onGenerate: (Int?) -> Unit) {
    val c = LocalProColors.current
    var step by remember { mutableIntStateOf(if (hasDraft) 0 else 1) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(24.dp),
        title = { Text(if (step == 0) "Start a workout" else "How long do you have?", style = ProType.cardTitle, color = c.text) },
        text = {
            if (step == 0) {
                Text(
                    "You already have a workout in progress. Continue it, or replace it with a fresh one?",
                    style = ProType.body2, color = c.text2,
                )
            } else {
                Column {
                    Text("We'll fit today's session to your available time — warm-up always stays.", style = ProType.body2, color = c.text2)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 25, 40).forEach { m -> TimeCell(m.toString(), Modifier.weight(1f), onClick = { onGenerate(m) }) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(60, 90).forEach { m -> TimeCell("$m", Modifier.weight(1f), onClick = { onGenerate(m) }) }
                        TimeCell("All day", Modifier.weight(1f), onClick = { onGenerate(null) }, showMin = false)
                    }
                }
            }
        },
        confirmButton = {
            if (step == 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // FIX (audit): Continue now actually navigates to the workout screen.
                    P4Button("Continue Workout", onClick = onContinue, style = BtnStyle.PRIMARY)
                    P4Button("Generate New", onClick = { step = 1 }, style = BtnStyle.SECONDARY)
                }
            }
        },
        dismissButton = {
            if (step == 0) P4Button("Cancel", onClick = onDismiss, style = BtnStyle.GHOST)
        },
    )
}

@Composable
private fun TimeCell(label: String, modifier: Modifier, onClick: () -> Unit, showMin: Boolean = true) {
    val c = LocalProColors.current
    Box(
        modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface2)
            .border(2.dp, c.hairline2, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(if (showMin) "$label min" else label, style = ProType.label, color = c.text, textAlign = TextAlign.Center)
    }
}
