package com.peakform.fitness

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import com.github.takahirom.roborazzi.captureRoboImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peakform.fitness.core.ActualPerformance
import com.peakform.fitness.ui.Fa
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.HistoryEntry
import com.peakform.fitness.core.Motivation
import com.peakform.fitness.core.Prescription
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.Synonyms
import com.peakform.fitness.core.UserProfile
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.core.WorkoutData
import com.peakform.fitness.core.WorkoutExercise
import com.peakform.fitness.core.WorkoutRecord
import com.peakform.fitness.core.WorkoutSummary
import com.peakform.fitness.ui.ProTheme
import com.peakform.fitness.ui.ThemeController
import com.peakform.fitness.ui.extras.BadgesScreen
import com.peakform.fitness.ui.extras.CycleScreen
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
import com.peakform.fitness.ui.extras.ThemePickerScreen
import com.peakform.fitness.ui.screens.DashboardScreen
import com.peakform.fitness.ui.screens.HistoryScreen
import com.peakform.fitness.ui.screens.LibraryScreen
import com.peakform.fitness.ui.screens.ProgressScreen
import com.peakform.fitness.ui.screens.RecoveryScreen
import com.peakform.fitness.ui.screens.SettingsScreen
import com.peakform.fitness.ui.screens.WorkoutScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Screenshot battery — every shipped screen rendered by the real Compose runtime on the
 * JVM (Robolectric native graphics) against seeded sample data, captured to PNG.
 * These are the images referenced from CHANGES.md (docs/verify/).
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w412dp-h892dp-420dpi", application = android.app.Application::class)
class ScreenshotTest {

    @get:Rule
    val compose = createAndroidComposeRule<TestHostActivity>()

    private fun outDir(): File {
        val d = File(System.getProperty("user.dir"), "build/screenshots")
        d.mkdirs()
        return d
    }

    private fun capture(name: String, idle: Boolean = true) {
        if (idle) compose.waitForIdle()
        // small settle for non-idle captures (infinite animations keep the idle detector busy)
        if (!idle) Thread.sleep(1200)
        val view = compose.activity.window.decorView
        val w = view.width.coerceAtLeast(1)
        val h = view.height.coerceAtLeast(1)
        val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
        view.draw(android.graphics.Canvas(bmp))
        val f = File(outDir(), "$name.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
        println("SCREENSHOT $name ${w}x${h} bytes=${f.length()} -> ${f.absolutePath}")
    }

    private fun seed() {
        val ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        Fa.appContext = ctx
        ProState.init(ctx)
        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        Vocab.ensure(ctx)
        Synonyms.ensure(ctx)
        Motivation.ensure(ctx)

        val user = UserProfile(name = "Alex Carter", birthDate = "1994-06-15", gender = "male",
            weight = 172.0, height = 178.0, experience = "intermediate", goal = "balanced",
            created = "2026-07-01T08:00:00.000Z")

        val days = listOf("2026-09-21", "2026-09-24", "2026-09-28", "2026-10-01", "2026-10-04")
        val workouts = days.mapIndexed { i, d ->
            WorkoutRecord(
                id = "workout_$i", date = "${d}T18:12:00.000Z", type = "Upper Body",
                name = "Upper Body — Strength",
                dateCompleted = "${d}T19:02:00.000Z",
                exercises = listOf(
                    WorkoutExercise(id = "bench_press", name = "Bench Press", muscleGroup = listOf("chest", "triceps"),
                        prescribed = Prescription(sets = 3, reps = "8-12", weight = 135.0),
                        actual = ActualPerformance(weight = 135.0, sets = 3, reps = listOf(10.0, 9.0, 8.0), rpe = com.peakform.fitness.core.ProJson.json.parseToJsonElement("[7.5]"), volume = 3645.0)),
                    WorkoutExercise(id = "barbell_row", name = "Barbell Row", muscleGroup = listOf("back"),
                        prescribed = Prescription(sets = 3, reps = "8-12", weight = 115.0),
                        actual = ActualPerformance(weight = 115.0, sets = 3, reps = listOf(10.0, 9.0, 9.0), volume = 3220.0)),
                ),
                summary = WorkoutSummary(totalVolume = 6865.0, averageRPE = 7.5, completedExercises = 2),
            )
        }
        val recs = mapOf(
            "bench_press" to ExerciseRecord(mu = 160.0, sigma2 = 9.0, nextWeight = 137.5, bestWeight = 160.0,
                history = listOf(HistoryEntry(date = "2026-10-04T18:30:00.000Z", weight = 135.0, sets = 3, reps = listOf(10.0, 9.0, 8.0), estimated1RM = 180.0))),
            "barbell_row" to ExerciseRecord(mu = 128.0, sigma2 = 6.0, nextWeight = 117.5, bestWeight = 128.0,
                history = listOf(HistoryEntry(date = "2026-10-04T18:50:00.000Z", weight = 115.0, sets = 3, reps = listOf(10.0, 9.0, 9.0), estimated1RM = 151.0))),
        )
        ProState.data = WorkoutData(user = user, workouts = workouts, exercises = recs)
        ProState.currentWorkout = WorkoutRecord(
            id = "draft_1", date = "2026-10-05T07:00:00.000Z", type = "Lower Body",
            name = "Lower Body — Hypertrophy", exercises = listOf(
                WorkoutExercise(id = "squat", name = "Back Squat", muscleGroup = listOf("quads"),
                    prescribed = Prescription(sets = 4, reps = "6-10", weight = 185.0)),
                WorkoutExercise(id = "romanian_deadlift", name = "Romanian Deadlift", muscleGroup = listOf("hamstrings"),
                    prescribed = Prescription(sets = 3, reps = "8-12", weight = 135.0)),
                WorkoutExercise(id = "plank", name = "Plank", muscleGroup = listOf("core_abs"),
                    prescribed = Prescription(sets = 3, reps = "hold", duration = 45.0), prescriptionType = "time", defaultDuration = 45.0),
            ),
        ).let { it.copy(draftStartedAt = it.date) }
        ProState.initialized = true
    }

    private fun render(content: @Composable () -> Unit) {
        compose.setContent { ProTheme(dark = ThemeController.dark, accent = ThemeController.accent()) { content() } }
    }

    @Test
    fun shot_01_dashboard() {
        seed()
        render { DashboardScreen(onStartWorkout = {}, onResumeWorkout = {}, onOpenSection = {}) }
        capture("01-dashboard")
    }

    @Test
    fun shot_02_workout_draft() {
        seed()
        render { WorkoutScreen(onRequireGenerate = {}, onOpenSection = {}) }
        capture("02-workout-draft")
    }

    @Test
    fun shot_03_history() {
        seed()
        render { HistoryScreen(onOpenSection = {}) }
        capture("03-history")
    }

    @Test
    fun shot_04_progress() {
        seed()
        render { ProgressScreen(onOpenSection = {}) }
        capture("04-progress")
    }

    @Test
    fun shot_05_recovery() {
        seed()
        render { RecoveryScreen(onOpenSection = {}, onRequireGenerate = {}) }
        capture("05-recovery")
    }

    @Test
    fun shot_06_library() {
        seed()
        render { LibraryScreen(onAddToWorkout = {}, onOpenWiki = {}) }
        capture("06-library")
    }

    @Test
    fun shot_07_settings() {
        seed()
        render { SettingsScreen(onOpenSection = {}) }
        capture("07-settings")
    }

    @Test
    fun shot_08_onboarding_step1() {
        seed()
        render { OnboardingWizard(onFinished = {}, onOpenLegal = {}) }
        capture("08-onboarding-step1")
    }

    @Test
    fun shot_09_health_screen() {
        seed()
        render { HealthScreenFlow(onDone = {}, onOpenLegal = {}) }
        capture("09-health-screen")
    }

    @Test
    fun shot_10_legal_center() {
        seed()
        render { LegalCenterScreen(initialDoc = "terms", onBack = {}) }
        capture("10-legal-center")
    }

    @Test
    fun shot_11_themes() {
        seed()
        render { ThemePickerScreen(onBack = {}) }
        capture("11-themes")
    }

    @Test
    fun shot_12_glossary() {
        seed()
        render { GlossaryScreen(onBack = {}) }
        capture("12-glossary")
    }

    @Test
    fun shot_13_library_studio() {
        seed()
        render { StudioScreen(onBack = {}, onToast = {}) }
        capture("13-library-studio")
    }

    @Test
    fun shot_14_snapshots_vault() {
        seed()
        render { SnapshotsScreen(onBack = {}, onToast = {}) }
        capture("14-snapshots-vault")
    }

    @Test
    fun shot_15_profiles() {
        seed()
        render { ProfilesScreen(onBack = {}, onToast = {}) }
        capture("15-profiles")
    }

    @Test
    fun shot_16_badges() {
        seed()
        render { BadgesScreen(onBack = {}) }
        capture("16-badges")
    }

    @Test
    fun shot_17_share_qr() {
        seed()
        render { ShareScreen(onBack = {}) }
        capture("17-share-qr")
    }

    @Test
    fun shot_18_quick_actions() {
        seed()
        render { QuickActionsScreen(onBack = {}, onAction = {}) }
        capture("18-quick-actions")
    }

    @Test
    fun shot_19_muscle_wiki() {
        seed()
        render { MuscleWikiScreen(muscle = "quadriceps", onBack = {}) }
        capture("19-muscle-wiki")
    }

    @Test
    fun shot_20_cycle() {
        seed()
        render { CycleScreen(onBack = {}) }
        capture("20-cycle")
    }

    @Test
    fun shot_21_streak_recovery() {
        seed()
        render { StreakRecoveryDialog(onDismiss = {}, onRecovered = {}) }
        capture("21-streak-recovery")
    }

    @Test
    fun shot_22_import_warning() {
        seed()
        render { ImportWarningDialog(raw = "```json\n{\"a\":1,}\n```", onConfirm = {}, onDismiss = {}) }
        capture("22-import-warning")
    }

    @Test
    fun shot_23_time_wizard() {
        seed()
        render { com.peakform.fitness.TimeWizardDialog(hasDraft = true, onDismiss = {}, onContinue = {}, onGenerate = {}) }
        capture("23-time-wizard")
    }
    @Test
    fun shot_24_confetti() {
        seed()
        render { com.peakform.fitness.ui.extras.ConfettiOverlay(onDone = {}) }
        compose.waitForIdle()
        capture("24-confetti")
    }

    // ---------- v1.4.0 — library media, 1RM Lab, gold identity ----------

    private fun benchPress(): com.peakform.fitness.core.LibraryExercise {
        val lib = com.peakform.fitness.engine.Library
        lib.ensure(compose.activity)
        return (lib.allLibraryExercises("gym").firstOrNull { it.name.contains("Bench Press") }
            ?: lib.allLibraryExercises("gym").first())
            .let { lib.augmented(it) }
    }

    @Test
    fun shot_25_library_media_card() {
        seed()
        val ex = benchPress()
        render {
            Column(
                Modifier.fillMaxSize().background(Color(0xFF0B0E14)).padding(14.dp),
            ) {
                com.peakform.fitness.ui.screens.LibraryCard(
                    ex = ex,
                    expanded = true,
                    starred = true,
                    onToggleStar = {}, onToggleExpand = {}, onAdd = {}, onLab = {}, onDetail = {},
                )
                Spacer(Modifier.height(14.dp))
                com.peakform.fitness.ui.screens.LibraryCard(
                    ex = com.peakform.fitness.engine.Library.augmented(
                        com.peakform.fitness.engine.Library.allLibraryExercises("gym")
                            .firstOrNull { it.name.contains("Deadlift", ignoreCase = true) } ?: ex,
                    ),
                    expanded = false,
                    starred = false,
                    onToggleStar = {}, onToggleExpand = {}, onAdd = {}, onLab = {}, onDetail = {},
                )
            }
        }
        capture("25-library-media-card")
    }

    @Test
    fun shot_26_one_rm_lab() {
        seed()
        val ex = benchPress()
        render {
            Column(Modifier.fillMaxSize().background(Color(0xFF0B0E14)).padding(14.dp)) {
                com.peakform.fitness.ui.extras.OneRmLabPanel(exercise = ex, onToast = {})
            }
        }
        capture("26-one-rm-lab")
    }

    @Test
    fun shot_27_gold_identity_header() {
        seed()
        render {
            Column(Modifier.fillMaxSize().background(Color(0xFF0B0E14))) {
                com.peakform.fitness.TopBar(onAvatarTap = {}, onFireTap = {}, onResumeTap = {})
                Spacer(Modifier.height(24.dp))
            }
        }
        capture("27-gold-identity-header")
    }
}
