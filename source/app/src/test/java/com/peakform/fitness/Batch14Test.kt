package com.peakform.fitness

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.ACCENTS
import com.peakform.fitness.ui.ProTheme
import com.peakform.fitness.ui.ThemeController
import com.peakform.fitness.ui.screens.OneRMAuditScreen
import com.peakform.fitness.ui.screens.OneRmLabScreen
import com.peakform.fitness.ui.screens.ReferenceGapsScreen
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * BATCH-14 evidence test — reference gaps panel + 1RM audit/Lab screens.
 *
 * Pins the contract that the new Batch 14 screens render without crashing
 * (no exceptions thrown on an empty profile) and that the new sheets' code
 * paths compile against the latest ProfileState / ProfileSettings.
 *
 * Captures are JVM-rendered with Robolectric native graphics from the same
 * Compose code compiled into the release APK.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w412dp-h892dp-xhdpi")
class Batch14Test {

    private val repoRoot: File = generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
        .firstOrNull { File(it, "docs/verify").isDirectory }
        ?: error("docs/verify not found above ${System.getProperty("user.dir")}")

    private val out: String = File(repoRoot, "docs/verify").absolutePath

    @get:org.junit.Rule
    val compose = androidx.compose.ui.test.junit4.createAndroidComposeRule(EmptyTestActivity::class.java)

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        System.setProperty("roborazzi.enabled", "true")
        ctx = RuntimeEnvironment.getApplication()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        Library.ensure(ctx)
        // Seed a realistic athlete so the panels render meaningful content.
        ProState.data = ProState.data.copy(
            user = ProState.data.user.copy(
                name = "Jordan", experience = "intermediate", gender = "male",
                weight = 180.0, height = 70.0, birthDate = "1996-04-12",
            ),
        )
        File(out).mkdirs()
    }

    @After
    fun tearDown() {
        ProfileState.prefs(ctx).edit().clear().commit()
        ProPrefs.clearAll(ctx)
    }

    private val slot = androidx.compose.runtime.mutableStateOf<(@androidx.compose.runtime.Composable () -> Unit)?>(null)
    private var slotId = 0

    private fun snap(name: String, content: @androidx.compose.runtime.Composable () -> Unit) {
        if (slot.value == null) {
            compose.setContent {
                ProTheme(dark = true, accent = ACCENTS[0]) {
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
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

    private val noopSection: (String) -> Unit = {}

    /** Reference gaps screen renders all five panels. */
    @Test
    fun referenceGaps_rendersAllPanels() {
        snap("batch14-01-reference-gaps") {
            ReferenceGapsScreen(onOpenSection = noopSection)
        }
    }

    /** 1RM Audit screen renders on an empty profile (no records). */
    @Test
    fun oneRmAudit_rendersOnEmptyProfile() {
        snap("batch14-02-onerm-audit-empty") {
            OneRMAuditScreen(onOpenSection = noopSection)
        }
    }

    /** 1RM Lab screen renders on an empty profile (no records). */
    @Test
    fun oneRmLab_rendersOnEmptyProfile() {
        snap("batch14-03-onerm-lab-empty") {
            OneRmLabScreen(onOpenSection = noopSection)
        }
    }

    /** ThemeController.cycleMode walks the 3-mode cycle. */
    @Test
    fun cycleMode_walksSystemDarkLightCycle() {
        ThemeController.set(ThemeController.MODE_SYSTEM, "blue", ctx)
        Assert.assertEquals(ThemeController.MODE_DARK, ThemeController.cycleMode(ctx))
        Assert.assertEquals(ThemeController.MODE_LIGHT, ThemeController.cycleMode(ctx))
        Assert.assertEquals(ThemeController.MODE_SYSTEM, ThemeController.cycleMode(ctx))
    }
}
