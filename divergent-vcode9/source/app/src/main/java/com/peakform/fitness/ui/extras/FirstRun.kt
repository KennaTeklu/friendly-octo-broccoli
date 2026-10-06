package com.peakform.fitness.ui.extras

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.Consent
import com.peakform.fitness.core.HealthScreen
import com.peakform.fitness.core.LegalDocs
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.ThemeController
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.SectionTitle

/** Consistent text input for the extras screens. */
@Composable
fun ProTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val c = LocalProColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(hint, color = c.text3, style = ProType.body2) },
        singleLine = true,
        textStyle = ProType.body2.copy(color = c.text),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = c.accent,
            unfocusedBorderColor = c.hairline2,
            focusedContainerColor = c.surface2,
            unfocusedContainerColor = c.surface2,
            cursorColor = c.accent,
        ),
    )
}

private fun birthMinor(b: String): Boolean {
    val year = b.trim().take(4).toIntOrNull() ?: return false
    return (2026 - year) in 0..17
}

/**
 * OnboardingWizard — legacy P4.Onboard 5 steps: name → birthdate/gender → reading level →
 * theme → consents. Suppressed whenever user data exists (legacy hasAnyUserData).
 */
@Composable
fun OnboardingWizard(onFinished: () -> Unit, onOpenLegal: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var step by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var birth by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf("male") }
    var draftVocab by remember { mutableIntStateOf(Vocab.level(ctx)) }
    var dark by remember { mutableStateOf(ThemeController.dark) }
    var accentId by remember { mutableStateOf(ThemeController.accentId) }
    var c1 by remember { mutableStateOf(false) }
    var c2 by remember { mutableStateOf(false) }
    var c3 by remember { mutableStateOf(false) }
    var c4 by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    val minor = birthMinor(birth.text)

    fun finish() {
        val u = ProState.data.user
        ProState.data = ProState.data.copy(user = u.copy(
            name = name.text.trim(),
            birthDate = birth.text.trim().takeIf { it.isNotBlank() },
            gender = gender,
            settings = u.settings.copy(darkMode = dark, theme = accentId),
        ))
        ProState.saveWorkoutData()
        Vocab.setLevel(ctx, draftVocab)
        com.peakform.fitness.core.Badges.countEvent(ctx, "vocabChanged")
        ProPrefs.put(ctx, "p4_theme", """{"mode":"${if (dark) "dark" else "light"}","accent":"$accentId"}""")
        ThemeController.set(if (dark) "dark" else "light", accentId)
        Consent.record(ctx, "terms", "Terms of Service accepted at onboarding")
        Consent.record(ctx, "privacy", "Privacy Policy accepted at onboarding")
        Consent.record(ctx, "waiver", "Liability Waiver accepted at onboarding")
        if (minor) Consent.record(ctx, "minors", "Parental consent recorded at onboarding")
        ProPrefs.put(ctx, "p4_onboarded", System.currentTimeMillis().toString())
        step = 6
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(28.dp))
        if (step < 6) {
            FaIcon("fa-dumbbell", size = 30.sp, tint = c.accent)
            Spacer(Modifier.height(8.dp))
            Text("Welcome to Pro", style = ProType.sectionTitle, color = c.text)
            Text("Step $step of 5", style = ProType.small, color = c.text3)
            Spacer(Modifier.height(18.dp))
        }
        when (step) {
            1 -> {
                Text("What should we call you?", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text("A name helps us greet you — even a nickname works.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(14.dp))
                ProTextField(name, { name = it }, "Your name or nickname")
            }
            2 -> {
                Text("About you", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text("Your birth date keeps guidance safe and age-right.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(14.dp))
                ProTextField(birth, { birth = it }, "Birth date (YYYY-MM-DD)", keyboardType = KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                Text("Gender", style = ProType.label, color = c.text2)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("male", "female", "other").forEach { g ->
                        val sel = gender == g
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) c.accentSoft else c.surface2)
                                .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                                .clickable { gender = g }
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                        ) {
                            Text(g.replaceFirstChar { it.uppercase() }, style = ProType.label, color = if (sel) c.accent else c.text2)
                        }
                    }
                }
            }
            3 -> {
                Text("How should the app talk to you?", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text("Pick your English level. Every label, tip and coach line adjusts — from super simple all the way to fancy.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(10.dp))
                val demo = Vocab.variant("hypertrophy", draftVocab)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.surface2)
                        .padding(12.dp),
                ) {
                    Text("Example: “This move builds $demo.”", style = ProType.body2, color = c.text, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Vocab.LEVELS.forEach { L ->
                        val sel = draftVocab == L.id
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) c.accentSoft else c.surface2)
                                .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                                .clickable { draftVocab = L.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Level ${L.id} — ${L.name}", style = ProType.label, color = if (sel) c.accent else c.text)
                                Text(L.desc, style = ProType.small, color = c.text3)
                            }
                            if (sel) FaIcon("fa-check", size = 13.sp, tint = c.accent)
                        }
                    }
                }
            }
            4 -> {
                Text("Pick your look", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text("Dark or light, plus an accent color. You can change this any time.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(false to "Light", true to "Dark").forEach { (d, label) ->
                        val sel = dark == d
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) c.accentSoft else c.surface2)
                                .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                                .clickable { dark = d }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                        ) { Text(label, style = ProType.label, color = if (sel) c.accent else c.text2) }
                    }
                }
                Spacer(Modifier.height(14.dp))
                val accents = com.peakform.fitness.ui.ACCENTS
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    accents.chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { a ->
                                val hex = if (dark) a.darkHex else a.lightHex
                                val sel = accentId == a.id
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(hex)
                                        .border(if (sel) 3.dp else 1.dp, if (sel) c.text else c.hairline2, RoundedCornerShape(10.dp))
                                        .clickable { accentId = a.id },
                                    contentAlignment = Alignment.Center,
                                ) { if (sel) FaIcon("fa-check", size = 14.sp, tint = Color.White) }
                            }
                        }
                    }
                }
            }
            5 -> {
                Text("The legal part", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text("Please tick every box — each one protects you.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(10.dp))
                ConsentRow("I accept the Terms of Service", c1, { c1 = it }, onOpenLegal, "terms")
                ConsentRow("I accept the Privacy Policy", c2, { c2 = it }, onOpenLegal, "privacy")
                ConsentRow("I accept the Liability Waiver & Assumption of Risk", c3, { c3 = it }, onOpenLegal, "waiver")
                if (minor) ConsentRow("A parent or guardian has reviewed these terms with me", c4, { c4 = it }, onOpenLegal, "minors")
            }
            6 -> {
                Spacer(Modifier.height(40.dp))
                Text("🎉", fontSize = 54.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "You're set, ${name.text.trim().ifBlank { "champ" }.split(" ").first()}!",
                    style = ProType.sectionTitle, color = c.text, textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text("Pro — everything unlocked. Your first workout is one tap away.", style = ProType.body2, color = c.text2, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                P4Button("Start my first workout", onClick = onFinished, style = BtnStyle.PRIMARY)
                Spacer(Modifier.height(8.dp))
                P4Button("Look around first", onClick = onFinished, style = BtnStyle.GHOST)
            }
        }

        if (step < 6) {
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                if (step > 1 && step < 6) P4Button("Back", onClick = { step-- }, style = BtnStyle.GHOST)
                if (step == 2) P4Button("Skip", onClick = { step = 3 }, style = BtnStyle.GHOST)
                Spacer(Modifier.weight(1f))
                when (step) {
                    1 -> P4Button("Next →", onClick = {
                        if (name.text.trim().isEmpty()) toast = "A name helps us greet you — even a nickname works."
                        else step = 2
                    }, style = BtnStyle.PRIMARY)
                    2 -> P4Button("Next →", onClick = {
                        if (birth.text.trim().isEmpty()) toast = "Please enter your birth date — it keeps guidance safe and age-right."
                        else step = 3
                    }, style = BtnStyle.PRIMARY)
                    3 -> P4Button("Next →", onClick = { step = 4 }, style = BtnStyle.PRIMARY)
                    4 -> P4Button("Next →", onClick = { step = 5 }, style = BtnStyle.PRIMARY)
                    5 -> P4Button("Finish", onClick = {
                        val need = if (minor) 4 else 3
                        val ok = listOf(c1, c2, c3) + if (minor) listOf(c4) else emptyList()
                        if (ok.count { it } < need) toast = "Please tick every box — each one protects you."
                        else finish()
                    }, style = BtnStyle.PRIMARY)
                }
            }
        }
        Spacer(Modifier.height(30.dp))
        toast?.let {
            Text(it, style = ProType.small, color = c.warn)
            LaunchedEffect(it) { kotlinx.coroutines.delay(2200); toast = null }
        }
    }
}

@Composable
private fun ConsentRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit, onOpenLegal: (String) -> Unit, doc: String) {
    val c = LocalProColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = onChange, colors = CheckboxDefaults.colors(checkedColor = c.accent))
        Column(Modifier.weight(1f)) {
            Text(label, style = ProType.small, color = c.text)
            Text("Read the $doc document", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onOpenLegal(doc) })
        }
    }
}

/** HealthScreenFlow — 7 general questions → joints/follow-ups → verdict A/B/C + waiver. */
@Composable
fun HealthScreenFlow(onDone: () -> Unit, onOpenLegal: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var general by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var followup by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var joints by remember { mutableStateOf(listOf<String>()) }
    var waiver by remember { mutableStateOf(false) }

    val verdict = remember(general, followup) { HealthScreen.computeTier(general, followup) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        SectionTitle("fa-heart-pulse", "Health screen")
        Text(
            "Three quick phases — general health, joints and follow-ups, then your verdict. " +
                "Answers stay on this device and are never shared.",
            style = ProType.body2, color = c.text2,
        )
        Spacer(Modifier.height(14.dp))

        when (step) {
            0 -> {
                HealthScreen.GENERAL.forEach { q ->
                    YesNoRow(q.text, q.important, general[q.id]) { v -> general = general + (q.id to v) }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(10.dp))
                P4Button("Continue", onClick = { step = 1 }, style = BtnStyle.PRIMARY, enabled = general.size >= HealthScreen.GENERAL.size)
            }
            1 -> {
                if (general["g4"] == true && joints.isEmpty()) {
                    Text("Which joints?", style = ProType.cardTitle, color = c.text)
                    Text("You flagged a joint issue. Pick the ones affected — we'll auto-skip movements that load them heavily.", style = ProType.body2, color = c.text2)
                    Spacer(Modifier.height(10.dp))
                    HealthScreen.JOINT_OPTIONS.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { j ->
                                val sel = j in joints
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) c.accentSoft else c.surface2)
                                        .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                                        .clickable { joints = if (sel) joints - j else joints + j }
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                ) { Text(j, style = ProType.label, color = if (sel) c.accent else c.text2) }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    P4Button("Continue", onClick = { step = 2 }, style = BtnStyle.PRIMARY, enabled = joints.isNotEmpty())
                } else {
                    val fu = verdict.followup
                    if (fu != null && (HealthScreen.FOLLOWUPS[fu]?.size ?: 0) > followup.size) {
                        Text("A few follow-ups", style = ProType.cardTitle, color = c.text)
                        Spacer(Modifier.height(8.dp))
                        HealthScreen.FOLLOWUPS[fu]?.forEach { q ->
                            if (followup[q.id] == null) {
                                YesNoRow(q.text, q.important, followup[q.id]) { v -> followup = followup + (q.id to v) }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        P4Button("Continue", onClick = {
                            val v = HealthScreen.computeTier(general, followup)
                            if (v.followup != null && v.followup != fu) {
                                // another follow-up chain remains; loop via recomposition
                            } else step = 2
                        }, style = BtnStyle.PRIMARY)
                    } else {
                        LaunchedEffect(Unit) { step = 2 }
                    }
                }
            }
            2 -> {
                val finalVerdict = HealthScreen.computeTier(general, followup)
                val tierColor = when (finalVerdict.tier) {
                    "A" -> c.ok
                    "B" -> c.warn
                    else -> c.bad
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(tierColor.copy(alpha = 0.12f))
                        .border(1.dp, tierColor, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    Column {
                        Text(finalVerdict.title, style = ProType.cardTitle, color = tierColor)
                        if (finalVerdict.description.isNotBlank()) {
                            Text(finalVerdict.description, style = ProType.body2, color = c.text2)
                        }
                        if (general["g4"] == true) {
                            HealthScreen.jointModifications(joints)?.let {
                                Text(it, style = ProType.small, color = c.text3)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (finalVerdict.tier != "C") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(c.accentSoft)
                            .padding(12.dp),
                    ) { Text(HealthScreen.RECOMMENDATION, style = ProType.small, color = c.text2) }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = waiver, onCheckedChange = { waiver = it }, colors = CheckboxDefaults.colors(checkedColor = c.accent))
                        Text(HealthScreen.WAIVER_TEXT, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                    }
                    Text("Read the waiver document", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onOpenLegal("waiver") })
                    Spacer(Modifier.height(10.dp))
                    P4Button("I accept — Continue", onClick = {
                        HealthScreen.save(ctx, finalVerdict.tier, general, followup, joints)
                        Consent.record(ctx, "waiver", "Health screen ${finalVerdict.tier} — waiver re-accepted")
                        onDone()
                    }, style = BtnStyle.PRIMARY, enabled = waiver)
                } else {
                    Text(HealthScreen.LOCK_NOTE, style = ProType.body2, color = c.text2)
                    Spacer(Modifier.height(10.dp))
                    Text(HealthScreen.RECOMMENDATION, style = ProType.small, color = c.text3)
                    Spacer(Modifier.height(10.dp))
                    P4Button("Save tier — I'll get clearance", onClick = {
                        HealthScreen.save(ctx, "C", general, followup, joints)
                        onDone()
                    }, style = BtnStyle.SECONDARY)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun YesNoRow(text: String, important: Boolean, answer: Boolean?, onChange: (Boolean) -> Unit) {
    val c = LocalProColors.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .padding(12.dp),
    ) {
        Text(
            if (important) "Important" else "Worth noting",
            style = ProType.small,
            color = if (important) c.bad else c.warn,
        )
        Spacer(Modifier.height(4.dp))
        Text(text, style = ProType.body2, color = c.text)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(true to "Yes", false to "No").forEach { (v, label) ->
                val sel = answer == v
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (sel) c.accentSoft else c.glass)
                        .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                        .clickable { onChange(v) }
                        .padding(horizontal = 22.dp, vertical = 8.dp),
                ) { Text(label, style = ProType.label, color = if (sel) c.accent else c.text2) }
            }
        }
    }
}

/** LegalCenterScreen — all 13 legacy legal documents with consent recording. */
@Composable
fun LegalCenterScreen(initialDoc: String?, onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val docs = remember { LegalDocs.load(ctx) }
    var current by remember { mutableStateOf(docs.firstOrNull { it.id == initialDoc }?.id ?: docs.firstOrNull()?.id ?: "") }
    val doc = docs.firstOrNull { it.id == current }

    LaunchedEffect(current) {
        if (current.isNotBlank()) Consent.record(ctx, current, "Legal Center — viewed ${doc?.title ?: ""}")
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaIcon("fa-scale-balanced", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Legal Center", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            docs.forEach { d ->
                val sel = d.id == current
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (sel) c.accentSoft else c.surface2)
                        .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(9.dp))
                        .clickable { current = d.id }
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                ) { Text(d.short, style = ProType.small, color = if (sel) c.accent else c.text2) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            if (doc != null) {
                Text(doc.title, style = ProType.sectionTitle, color = c.text)
                Spacer(Modifier.height(10.dp))
                doc.sections.forEach { sec ->
                    Text(sec.h, style = ProType.cardTitle, color = c.text)
                    Spacer(Modifier.height(4.dp))
                    sec.p.forEach { p ->
                        Text(p, style = ProType.body2, color = c.text2)
                        Spacer(Modifier.height(6.dp))
                    }
                    sec.ul.forEach { item ->
                        Row {
                            Text("•  ", style = ProType.body2, color = c.accent)
                            Text(item, style = ProType.body2, color = c.text2)
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                }
                val accepted = Consent.hasAccepted(ctx, doc.id)
                Text(
                    if (accepted) "You have accepted this document (recorded in your consent log)." else "Not accepted yet.",
                    style = ProType.small, color = if (accepted) c.ok else c.text3,
                )
                Spacer(Modifier.height(10.dp))
                P4Button("I accept this document", onClick = {
                    Consent.record(ctx, doc.id, "Legal Center — accepted ${doc.title}")
                }, style = BtnStyle.SECONDARY)
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}
