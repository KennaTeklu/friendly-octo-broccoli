package com.peakform.fitness

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.peakform.fitness.core.Badges
import com.peakform.fitness.core.Motivation
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.LongevityReportDialog
import com.peakform.fitness.ui.ProTheme
import com.peakform.fitness.ui.ThemeController
import com.peakform.fitness.ui.screens.DashboardScreen
import com.peakform.fitness.ui.screens.NotificationsCardBody
import com.peakform.fitness.ui.screens.SettingsGroupCard
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * BATCH-4C screenshot battery — one screenshot per shipped feature:
 *   1. Dashboard with Quote / Win-of-the-Day / Coach Tips cards
 *   2. Longevity Report modal (aging risks + breakdown + recommendations)
 *   3. Settings Notifications card with "Send test notification" button
 *   4. Library screen honoring p4_lib_group pref (opens to Equipment grouping)
 *
 * Method note (same as Batch 4B): captures are JVM-rendered with Robolectric
 * native graphics from the SAME Compose code compiled into the release APK.
 * This sandbox has no KVM, so the Android emulator cannot complete a TCG boot.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w412dp-h892dp-xhdpi")
class Batch4CScreenshots {

    private val repoRoot: File = generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
        .firstOrNull { File(it, "docs/verify").isDirectory }
        ?: File(System.getProperty("user.dir")).parentFile?.parentFile ?: File(System.getProperty("user.dir"))

    private val out: String = File(repoRoot, "docs/verify").absolutePath

    @get:org.junit.Rule
    val compose = createAndroidComposeRule(EmptyTestActivity::class.java)

    private lateinit var ctx: Context

    @Before
    fun setup() {
        System.setProperty("roborazzi.enabled", "true")
        ctx = RuntimeEnvironment.getApplication()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        Library.ensure(ctx)
        Vocab.init(ctx)
        Badges.init(ctx)
        // Seed a realistic athlete so dashboard cards have content
        ProState.data = ProState.data.copy(
            user = ProState.data.user.copy(
                name = "Jordan", experience = "intermediate", gender = "male",
                weight = 180.0, height = 70.0, birthDate = "1996-04-12",
            ),
        )
        ProfileState.remove(ctx, ProfileState.K_HEALTH_INTRO_SEEN)
        ProfileState.remove(ctx, ProfileState.K_ONBOARDED)
        ProfileState.remove(ctx, ProfileState.K_OB_DRAFT)
        java.io.File(out).mkdirs()
    }

    private val slot = androidx.compose.runtime.mutableStateOf<(@androidx.compose.runtime.Composable () -> Unit)?>(null)
    private var slotId = 0

    private fun snap(name: String, content: @androidx.compose.runtime.Composable () -> Unit) {
        if (slot.value == null) {
            compose.setContent {
                ProTheme(dark = true, accent = ThemeController.accent(), dynamic = ThemeController.dynamic) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        slot.value?.invoke()
                    }
                }
            }
        }
        slotId++
        val wrapped: @androidx.compose.runtime.Composable () -> Unit = {
            androidx.compose.runtime.key(slotId) { content() }
        }
        slot.value = wrapped
        compose.waitForIdle()
        compose.runOnUiThread {
            val view = compose.activity.window.decorView
            val w = view.width.coerceAtLeast(412)
            val h = view.height.coerceAtLeast(892)
            val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bmp)
            view.draw(canvas)
            File("$out/$name.png").outputStream().use { os ->
                bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, os)
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun CardHost(
        title: String,
        icon: String,
        body: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
        ) {
            SettingsGroupCard(title = title, icon = icon, expanded = true, onToggle = {}) { body() }
        }
    }

    private val noop: (String) -> Unit = {}

    // ---------- 1. Dashboard with Quote / Win / Tips cards ----------

    @Test
    fun dashboardQuoteWinTips() {
        // Clear any persisted daily quote so the card renders fresh
        ProPrefsRemove(ctx, "p4_daily_quote_" + com.peakform.fitness.core.ProState.todayLocal())
        snap("batch4c-01-dashboard-quote-win-tips") {
            DashboardScreen(
                onStartWorkout = {},
                onResumeWorkout = {},
                onOpenSection = {},
                onRequireGenerate = {},
            )
        }
    }

    // ---------- 2. Longevity Report modal ----------

    @Test
    fun longevityReportModal() {
        snap("batch4c-02-longevity-report-modal") {
            LongevityReportDialog(
                onDismiss = {},
                onLongevityWorkout = {},
            )
        }
    }

    // ---------- 3. Settings Notifications card with test-notif button ----------

    @Test
    fun settingsNotificationsTestButton() {
        snap("batch4c-03-settings-notifications-test-button") {
            CardHost("Notifications", "fa-bell") { NotificationsCardBody(noop) }
        }
    }

    // ---------- 4. Library honoring p4_lib_group pref ----------

    @Test
    fun libraryHonorsGroupPref() {
        // Set the pref to "equipment" and verify the Library opens to that grouping.
        // Screenshot captures the group-by chip highlighting "Equipment".
        com.peakform.fitness.core.ProPrefs.put(ctx, "p4_lib_group", "equipment")
        snap("batch4c-04-library-group-pref") {
            com.peakform.fitness.ui.screens.LibraryScreen(onAddToWorkout = {})
        }
    }
}

// Helper to remove a pref (ProPrefs has put/get but no remove in the public API;
// use the underlying SharedPreferences directly).
private fun ProPrefsRemove(ctx: Context, key: String) {
    try {
        val prefs = ctx.getSharedPreferences("p4_ls", android.content.Context.MODE_PRIVATE)
        prefs.edit().remove(key).apply()
    } catch (_: Exception) {}
}
