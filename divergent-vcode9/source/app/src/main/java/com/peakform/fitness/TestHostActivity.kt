package com.peakform.fitness

import androidx.activity.ComponentActivity

/**
 * TestHostActivity — minimal ComponentActivity host for the Robolectric screenshot
 * battery (docs/verify/). Not exported, not launchable, not part of any user flow;
 * it exists so the Compose UI test rule can host composables on the JVM.
 */
class TestHostActivity : ComponentActivity()
