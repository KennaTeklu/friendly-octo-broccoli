package com.peakform.fitness.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.*
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Settings — 1:1 port of the P4 10-group accordion (scr-settings.md §1) PLUS the
 * structural fixes demanded by the P1–P4 audit:
 *  - a "Training preferences" card resurfaces Height / Experience / Goal / Workout days /
 *    Rest time / Progression rate (unreachable originals in legacy) with MERGE semantics
 *  - Danger zone with type-RESET confirm + optional pre-reset backup (resurfaces the
 *    orphaned Reset All Data) and full purge incl. profile/vault keys
 *  - Profile picker grid (emoji animals) with switch/add/delete
 *  - Import/Export: Format A + Complete-Backup-v1 via SAF
 */

private val SETTINGS_CARDS = listOf(
    "Personal info" to "fa-user",
    "Health & clearance" to "fa-notes-medical",
    "Appearance" to "fa-palette",
    "Reading level" to "fa-language",
    "Language" to "fa-earth-americas",
    "Power user" to "fa-bolt",
    "Exercise library management" to "fa-dumbbell",
    "Plan & subscription" to "fa-crown",
    "Data & backup" to "fa-database",
    "Training environment" to "fa-house-chimney",
    "Training preferences" to "fa-sliders",
    "Privacy & safety" to "fa-shield-halved",
    "About" to "fa-circle-info",
    "Legal" to "fa-scale-balanced",
    "Notifications" to "fa-bell",
    "Profiles" to "fa-user-group",
    "Danger zone" to "fa-triangle-exclamation",
)

@Composable
fun SettingsScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val ver = remember { mutableIntStateOf(0) }
    var openCard by remember { mutableStateOf<String?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }
    var showProfiles by remember { mutableStateOf(false) }
    var showLegal by remember { mutableStateOf(false) }
    var resetFlow by remember { mutableStateOf(false) }
    var pendingExport: JsonObject? by remember { mutableStateOf<JsonObject?>(null) }
    var pendingExportText: Pair<String, String>? by remember { mutableStateOf(null) } // kind to "text/csv"|"application/json" + payload (feature 1/4/5/6)
    var pendingImport = remember { mutableStateOf(false) }
    // BATCH-2A: guard + merge-report state for every import path
    var pendingImportKind by remember { mutableStateOf<String?>(null) } // backup | exercises | phrasebook | bundle
    var pendingImportRaw by remember { mutableStateOf<String?>(null) }
    var importReport by remember { mutableStateOf<Backup.ImportResult?>(null) }
    var listReport by remember { mutableStateOf<List<String>?>(null) }
    var bundleReport by remember { mutableStateOf<Backup.BundleImportResult?>(null) }

    fun refresh() { ver.intValue++; ProState.notifyChanged() }

    // SAF launchers
    val cs = rememberCoroutineScope()
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val data = pendingExport
        if (uri != null && data != null) {
            val ok = Backup.writeJsonToUri(ctx, uri, data)
            // FIX (audit): only stamp the backup timestamp when the export actually succeeded,
            // otherwise a failed export suppresses future "no backup" nudges.
            if (ok) ProPrefs.put(ctx, "p4_last_backup", ProState.nowIso())
            toast = if (ok) "Backup exported ✓" else "Export failed"
        }
        pendingExport = null
    }
    val textExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/*")) { uri ->
        val (kind, payload) = pendingExportText ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            val ok = Backup.writeTextToUri(ctx, uri, payload)
            toast = if (ok) "$kind exported ✓" else "Export failed"
            if (ok && kind == "CSV history") {
                // feature 1: SAF save PLUS system share
                try {
                    val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(android.content.Intent.EXTRA_TEXT, payload)
                    }
                    ctx.startActivity(android.content.Intent.createChooser(send, "Share workout history CSV"))
                } catch (_: Exception) {}
            }
        }
        pendingExportText = null
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val raw = Backup.readJsonFromUri(ctx, uri)
                if (raw != null) {
                    // feature 8: guard EVERY import — only a red confirm proceeds
                    pendingImportKind = "backup"
                    pendingImportRaw = raw
                } else toast = "Could not read file"
            }
        }
    }
    val fileImportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val raw = Backup.readJsonFromUri(ctx, uri)
                if (raw != null) {
                    val kind = pendingImportKind ?: "backup"
                    pendingImportKind = kind
                    pendingImportRaw = raw
                } else toast = "Could not read file"
            }
        }
    }

    val visibleCards = if (search.length >= 2) {
        val q = search.lowercase()
        SETTINGS_CARDS.filter { (title, _) -> title.lowercase().contains(q) }
    } else SETTINGS_CARDS

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-cog", "Settings")

        // search
        ProTextField(value = search, onValueChange = { search = it }, placeholder = "Search settings…")

        visibleCards.forEach { (title, icon) ->
            SettingsGroupCard(
                title = title,
                icon = icon,
                expanded = openCard == title || search.length >= 2,
                onToggle = { openCard = if (openCard == title) null else title },
            ) {
                when (title) {
                    "Personal info" -> PersonalCardBody { toast = it; refresh() }
                    "Health & clearance" -> HealthClearanceCardBody({ toast = it; refresh() }, onOpenSection)
                    "Privacy & safety" -> PrivacyCardBody { toast = it }
                    "About" -> AboutCardBody { toast = it }
                    "Appearance" -> AppearanceCardBody { toast = it }
                    "Reading level" -> ReadingCardBody { toast = it }
                    "Language" -> LanguageCardBody { toast = it }
                    "Power user" -> PowerCardBody { toast = it; refresh() }
                    "Exercise library management" -> Column {
                        Text("Author custom movements, merge imports, export your library.", style = ProType.small, color = c.text3)
                        Spacer(Modifier.height(8.dp))
                        P4Button("Open Library Studio", icon = "fa-flask-vial", style = BtnStyle.INFO, modifier = Modifier.fillMaxWidth()) {
                            onOpenSection("studio")
                        }
                    }
                    "Plan & subscription" -> Column {
                        Text("Pro — everything unlocked. Thank you!", style = ProType.body2, color = c.text2)
                        Spacer(Modifier.height(8.dp))
                        P4Button("Manage plan", icon = "fa-crown", style = BtnStyle.PRIMARY) { toast = "Everything is unlocked — no plan needed." }
                    }
                    "Data & backup" -> Column {
                        DataCardBody(
                        onExport = {
                            // FIX (audit): vault read used runBlocking on the main thread (ANR class).
                            cs.launch {
                                val json = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    Backup.exportCompleteV1(ctx)
                                }
                                pendingExport = json
                                exportLauncher.launch(ProState.exportFileName("PeakForm-Complete-Backup"))
                            }
                        },
                        onExportA = {
                            val json = Backup.exportFormatA(ctx)
                            pendingExport = json
                            exportLauncher.launch(ProState.exportFileName("export"))
                        },
                        onImport = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
                        onExportCsv = {
                            val (csv, rows) = Exporter.historyCsvPerSet()
                            pendingExportText = "CSV history" to csv
                            textExportLauncher.launch(ProState.exportFileName("history").replace(".json", ".csv"))
                            if (rows == 0) toast = "No logged sets yet — the CSV will only hold the header"
                        },
                        onExportLibrary = {
                            val json = Studio.exportLibrary(ctx)
                            pendingExportText = "Custom exercises" to json
                            textExportLauncher.launch(ProState.exportFileName("custom-exercises"))
                        },
                        onImportLibrary = {
                            pendingImportKind = "exercises"
                            fileImportLauncher.launch(arrayOf("application/json", "text/plain"))
                        },
                        onExportConsent = {
                            val json = Consent.exportLog(ctx)
                            pendingExportText = "Consent log" to json
                            textExportLauncher.launch(ProState.exportFileName("consent-log"))
                        },
                        onExportFeedback = {
                            val json = Exporter.feedbackJson(ctx)
                            pendingExportText = "Feedback" to json
                            textExportLauncher.launch(ProState.exportFileName("feedback"))
                        },
                        onExportPhrasebook = {
                            val json = Vocab.exportPhrasebook(ctx)
                            pendingExportText = "Phrasebook" to json
                            textExportLauncher.launch(ProState.exportFileName("phrasebook-L${Vocab.currentLevel(ctx)}"))
                        },
                        onImportPhrasebook = {
                            pendingImportKind = "phrasebook"
                            fileImportLauncher.launch(arrayOf("application/json", "text/plain"))
                        },
                        onSnapshots = { onOpenSection("snapshots") },
                        onPasteJson = { onOpenSection("pasteimport") },
                        onExportBundle = {
                            cs.launch {
                                val json = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    Backup.exportBundleV1(ctx)
                                }
                                pendingExport = json
                                exportLauncher.launch(ProState.exportFileName("app-bundle"))
                            }
                        },
                        onImportBundle = {
                            pendingImportKind = "bundle"
                            fileImportLauncher.launch(arrayOf("application/json", "text/plain"))
                        },
                        onFeedback = { onOpenSection("feedback") },
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Cloud backup: exports go through the system file picker — choose your Google Drive folder there.", style = ProType.small, color = c.text3)
                    }
                    "Training environment" -> TrainingEnvCardBody { toast = it; refresh() }
                    "Training preferences" -> TrainingPrefsCardBody { toast = it; refresh() }
                    "Legal" -> Column {
                        P4Button("Legal Center (13 documents)", icon = "fa-book", style = BtnStyle.INFO) { showLegal = true }
                    }
                    "Notifications" -> NotificationsCardBody { toast = it }
                    "Profiles" -> Column {
                        val profiles = remember(ver.intValue) { Profiles.list(ctx) }
                        val active = remember(ver.intValue) { Profiles.activeId(ctx) }
                        profiles.forEach { p ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(p.emoji ?: "🏋️", fontSize = 22.sp)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(p.name, style = ProType.label, color = if (p.id == active) c.accent else c.text)
                                    Text(if (p.id == active) "Active" else "Available", style = ProType.small, color = c.text3)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        P4Button("Manage profiles (switch without restart)", icon = "fa-user-group", style = BtnStyle.INFO, modifier = Modifier.fillMaxWidth()) {
                            onOpenSection("profiles")
                        }
                    }
                    "Danger zone" -> Column {
                        Text("Reset all data — workouts, exercises, profile stats and snapshots. Your license and app settings are preserved.", style = ProType.small, color = c.text3)
                        Spacer(Modifier.height(10.dp))
                        P4Button("Reset all data", icon = "fa-trash", style = BtnStyle.DANGER) { resetFlow = true }
                    }
                }
            }
        }
        Spacer(Modifier.height(120.dp))
    }

    toast?.let { t ->
        LaunchedEffect(t) {
            kotlinx.coroutines.delay(2400)
            toast = null
        }
        ToastBanner(t)
    }

    if (resetFlow) {
        ResetFlowDialog(onCancel = { resetFlow = false }, onDone = { msg ->
            resetFlow = false
            toast = msg
            refresh()
        }, ctx = ctx)
    }

    // BATCH-2A feature 8: pre-import guard — red confirm before ANY import proceeds
    val guardKind = pendingImportKind
    val guardRaw = pendingImportRaw
    if (guardKind != null && guardRaw != null) {
        ImportGuardDialog(
            detail = when (guardKind) {
                "backup" -> "This merges the selected backup into your current data."
                "exercises" -> "Custom exercises are MERGED — same name updates, new name adds. Existing data is never replaced."
                "phrasebook" -> "The phrasebook merges into the reading-level ladders for the current level."
                "bundle" -> "A full app bundle RESTORES everything: workouts, exercises, library, profiles, snapshots, settings and consent logs."
                else -> "Proceed with the import?"
            },
            onConfirm = {
                val kind = pendingImportKind
                val raw = pendingImportRaw
                pendingImportKind = null
                pendingImportRaw = null
                when (kind) {
                    "backup" -> scope.launch {
                        val res = Backup.import(ctx, raw ?: "")
                        // feature 11: merge report surfaced as a dialog on EVERY import
                        importReport = res
                        refresh()
                    }
                    "exercises" -> {
                        // feature 3: heal + merge + added/updated/rejected counts
                        listReport = Studio.importAndMerge(ctx, raw ?: "")
                        Badges.bumpCounter(ctx, "imports")
                        refresh()
                    }
                    "phrasebook" -> {
                        // feature 6: merge with added/updated/rejected counts
                        val r = Vocab.importPhrasebookReport(ctx, raw ?: "")
                        toast = "Phrasebook merged — added ${r.added}, updated ${r.updated}, rejected ${r.rejected}"
                        refresh()
                    }
                    "bundle" -> {
                        // feature 10: full restore with per-section details
                        bundleReport = Backup.importBundleV1(ctx, raw ?: "")
                        refresh()
                    }
                }
            },
            onDismiss = {
                pendingImportKind = null
                pendingImportRaw = null
            },
        )
    }

    // feature 11: merge report dialog after every backup import
    importReport?.let { r ->
        MergeReportDialog(
            result = r,
            onClose = {
                importReport = null
                toast = if (r.ok) "Import finished" else "Import failed"
            },
        )
    }

    // features 3/10: line-list report dialogs (custom exercises / bundle restore)
    listReport?.let { lines ->
        ListReportDialog(title = "Custom exercises merge report", lines = lines, onClose = { listReport = null })
    }
    bundleReport?.let { r ->
        ListReportDialog(
            title = if (r.ok) "App bundle restored" else "Bundle import failed",
            lines = if (r.ok) listOf(r.message) + r.details else listOf(r.message),
            onClose = { bundleReport = null },
        )
    }

    if (showLegal) {
        LegalCenterDialog(onClose = { showLegal = false })
    }
}

/** Line-list report dialog (custom-exercise merge report, bundle restore details). */
@Composable
fun ListReportDialog(title: String, lines: List<String>, onClose: () -> Unit) {
    val c = LocalProColors.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onClose,
        containerColor = c.glass2,
        shape = RoundedCornerShape(28.dp),
        title = { Text(title, style = ProType.cardTitle, color = c.text) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()).heightIn(max = 420.dp)) {
                lines.forEach { line -> Text(line, style = ProType.small, color = c.text2, modifier = Modifier.padding(vertical = 1.dp)) }
            }
        },
        confirmButton = { P4Button("Done", onClick = onClose, style = BtnStyle.PRIMARY, minHeight = 40) },
    )
}

@Composable
fun SettingsGroupCard(title: String, icon: String, expanded: Boolean, onToggle: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val c = LocalProColors.current
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.surface)
            .border(1.dp, c.hairline, shape)
    ) {
        Row(
            Modifier.fillMaxWidth().clickable { onToggle() }.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            com.peakform.fitness.ui.FaIcon(icon, size = 14.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Text(title, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
            com.peakform.fitness.ui.FaIcon(if (expanded) "fa-chevron-up" else "fa-chevron-down", size = 12.sp, tint = c.text3)
        }
        AnimatedVisibility(visible = expanded) {
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp), content = content)
        }
    }
}

// ---------------- card bodies ----------------

@Composable
private fun PersonalCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val user = ProState.data.user
    var name by remember { mutableStateOf(user.name) }
    var birth by remember { mutableStateOf(user.birthDate ?: "") }
    var gender by remember { mutableStateOf(if (user.gender == "male" || user.gender == "female" || user.gender == "other") user.gender else "unspecified") }
    var weight by remember { mutableStateOf(user.weight?.toInt()?.toString() ?: "") }

    ProTextField(value = name, onValueChange = { name = it }, placeholder = "Name")
    Spacer(Modifier.height(8.dp))
    Text("Birth date (YYYY-MM-DD)", style = ProType.small, color = c.text3)
    ProTextField(value = birth, onValueChange = { birth = it }, placeholder = "1990-06-15")
    Spacer(Modifier.height(8.dp))
    Text("Gender", style = ProType.small, color = c.text3)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        listOf("unspecified" to "Prefer not to say", "male" to "Male", "female" to "Female", "other" to "Other").forEach { (value, label) ->
            val active = gender == value
            Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (active) c.accentSoft else c.surface2).border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(10.dp)).clickable { gender = value }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text(label, fontSize = 12.sp, color = if (active) c.accent else c.text2, maxLines = 1)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("Body weight (lbs)", style = ProType.small, color = c.text3)
    ProTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() } }, placeholder = "150-500")
    Spacer(Modifier.height(12.dp))
    P4Button("Save", icon = "fa-save", modifier = Modifier.fillMaxWidth()) {
        val d = ProState.data
        val w = weight.toDoubleOrNull()?.coerceIn(50.0, 500.0)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val hist = user.bodyWeightHistory.toMutableList()
        if (w != null && w != user.weight) hist.add(BodyWeightEntry(date = today, weight = w, source = "entry"))
        ProState.data = d.copy(user = user.copy(
            name = name.trim(),
            birthDate = birth.takeIf { it.isNotBlank() },
            gender = gender,
            weight = w ?: user.weight,
            bodyWeightHistory = hist.takeLast(100),
        ))
        ProState.saveWorkoutData()
        onToast("Personal info saved")
    }
}

@Composable
fun AppearanceCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    Column { appearanceInner(onToast) }
}

@Composable
private fun appearanceInner(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val themeRaw = ProPrefs.get(LocalContext.current, "p4_theme")
    var mode by remember { mutableStateOf(if (themeRaw?.contains("\"light\"") == true) "light" else "dark") }
    var accentId by remember { mutableStateOf(ThemeController.accentId) }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("dark" to "fa-moon", "light" to "fa-sun").forEach { (m, ic) ->
            val active = mode == m
            Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (active) c.accentSoft else c.surface2).border(2.dp, if (active) c.accent else c.hairline2, RoundedCornerShape(10.dp)).clickable {
                mode = m
                ThemeController.set(m, accentId)
                ProPrefs.put(ctx, "p4_theme", """{"mode":"$m","accent":"$accentId"}""")
                onToast("Theme updated — $m · ${ACCENTS.firstOrNull { it.id == accentId }?.name}")
            }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    com.peakform.fitness.ui.FaIcon(ic, size = 13.sp, tint = if (active) c.accent else c.text2)
                    Spacer(Modifier.width(6.dp))
                    Text(m.replaceFirstChar { it.uppercase() }, fontSize = 13.sp, color = if (active) c.accent else c.text2)
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    // 16 swatches, 8 per row
    ACCENTS.chunked(8).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            row.forEach { a ->
                val active = accentId == a.id
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(if (ThemeController.dark) a.darkHex else a.lightHex)
                        .border(if (active) 3.dp else 1.dp, if (active) c.text else c.hairline2, androidx.compose.foundation.shape.CircleShape)
                        .clickable {
                            accentId = a.id
                            ThemeController.set(mode, a.id)
                            ProPrefs.put(ctx, "p4_theme", """{"mode":"$mode","accent":"$accentId"}""")
                            onToast("Theme updated — $mode · ${a.name}")
                        }
                )
            }
        }
    }
    val accentName = ACCENTS.firstOrNull { it.id == accentId }?.name ?: ""
    Text(accentName, fontSize = 12.sp, color = c.text2, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
    Text("All 16 colors available", style = ProType.small, color = c.text3, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
    ToggleRow("Gym Mode", "Giant buttons, no waiting on animations", checked = ProPrefs.get(LocalContext.current, "p4_gym") == "on") { on ->
        ProPrefs.put(ctx, "p4_gym", if (on) "on" else "off")
        onToast(if (on) "Gym Mode ON — huge buttons, no waiting on animations." else "Gym Mode off.")
    }
    ToggleRow("Material You color", "Tint Pro with your wallpaper palette (Android 12+)", checked = ProPrefs.get(ctx, "p4_dynamic") == "on") { on ->
        ProPrefs.put(ctx, "p4_dynamic", if (on) "on" else "off")
        onToast(if (on) "Material You color on — reopen the app to apply fully." else "Material You color off.")
    }
}

@Composable
private fun ReadingCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val levels = Library.readingLevels
    var level by remember { mutableIntStateOf(ProPrefs.get(ctx, "p4_vocab")?.toIntOrNull() ?: 5) }
    Text("The app speaks at your level — from super simple (1) to fancy English (10).", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(8.dp))
    levels.forEach { l ->
        val active = level == l.id
        Row(Modifier.fillMaxWidth().clickable {
            level = l.id
            ProPrefs.put(ctx, "p4_vocab", l.id.toString())
            onToast("Words set to Level ${l.id} — ${l.name}")
        }.padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(18.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if (active) c.accent else c.surface3).border(1.dp, c.hairline2, androidx.compose.foundation.shape.CircleShape))
            Spacer(Modifier.width(10.dp))
            Text("L${l.id} — ${l.name}", style = ProType.body2, color = if (active) c.accent else c.text2)
        }
    }
}

@Composable
private fun LanguageCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var lang by remember { mutableStateOf(ProPrefs.get(ctx, "p4_lang") ?: "auto") }
    Text("Also works with your system translation — labels and buttons are structured for clean translation.", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(8.dp))
    Library.langs.forEach { l ->
        val active = lang == l.code
        Row(Modifier.fillMaxWidth().clickable {
            lang = l.code
            ProPrefs.put(ctx, "p4_lang", l.code)
            onToast("Language set to ${l.name}.")
        }.padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(14.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if (active) c.accent else c.surface3))
            Spacer(Modifier.width(10.dp))
            Text(l.name, style = ProType.body2, color = if (active) c.accent else c.text2)
        }
    }
}

@Composable
private fun PowerCardBody(onToast: (String) -> Unit) {
    val ctx = LocalContext.current
    val raw = ProPrefs.get(ctx, "p4_power_settings")
    val power = remember(raw) {
        try {
            val o = ProJson.json.parseToJsonElement(raw ?: "{}") as JsonObject
            Triple(
                (o["autoRest"] as? JsonPrimitive)?.content != "false",
                (o["haptics"] as? JsonPrimitive)?.content != "false",
                (o["swipe"] as? JsonPrimitive)?.content != "false",
            )
        } catch (_: Exception) { Triple(true, true, true) }
    }
    fun save(autoRest: Boolean, haptics: Boolean, swipe: Boolean) {
        val json = """{"autoRest":$autoRest,"haptics":$haptics,"swipe":$swipe,"dock":true}"""
        ProPrefs.put(ctx, "p4_power_settings", json)
    }
    ToggleRow("Auto rest timer after logging effort", checked = power.first) { on ->
        save(on, power.second, power.third); onToast(if (on) "Enabled — saved" else "Disabled — saved")
    }
    ToggleRow("Haptic buzz (vibration)", checked = power.second) { on ->
        save(power.first, on, power.third); onToast(if (on) "Enabled — saved" else "Disabled — saved")
    }
    ToggleRow("Swipe between pages — coming soon", checked = power.third) { on ->
        save(power.first, power.second, on); onToast(if (on) "Enabled — saved" else "Disabled — saved")
    }
}

@Composable
private fun DataCardBody(
    onExport: () -> Unit,
    onExportA: () -> Unit,
    onImport: () -> Unit,
    onExportCsv: () -> Unit,
    onExportLibrary: () -> Unit,
    onImportLibrary: () -> Unit,
    onExportConsent: () -> Unit,
    onExportFeedback: () -> Unit,
    onExportPhrasebook: () -> Unit,
    onImportPhrasebook: () -> Unit,
    onSnapshots: () -> Unit,
    onPasteJson: () -> Unit,
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onFeedback: () -> Unit,
) {
    val c = LocalProColors.current
    // BATCH-2A UI contract — exact button order from BATCH-2A.md § UI
    P4Button("Export full backup", icon = "fa-cloud-arrow-down", style = BtnStyle.SUCCESS, modifier = Modifier.fillMaxWidth()) { onExport() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export workout JSON (Format A)", icon = "fa-download", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportA() }
    Spacer(Modifier.height(8.dp))
    P4Button("Import backup", icon = "fa-upload", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onImport() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export workout history as CSV", icon = "fa-file-csv", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportCsv() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export custom exercises", icon = "fa-file-export", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportLibrary() }
    Spacer(Modifier.height(8.dp))
    P4Button("Import custom exercises", icon = "fa-file-import", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onImportLibrary() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export consent log", icon = "fa-scale-balanced", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportConsent() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export feedback", icon = "fa-comment-dots", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportFeedback() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export phrasebook", icon = "fa-language", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onExportPhrasebook() }
    Spacer(Modifier.height(8.dp))
    P4Button("Import phrasebook", icon = "fa-language", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onImportPhrasebook() }
    Spacer(Modifier.height(8.dp))
    P4Button("Snapshots", icon = "fa-camera", style = BtnStyle.INFO, modifier = Modifier.fillMaxWidth()) { onSnapshots() }
    Spacer(Modifier.height(8.dp))
    P4Button("Paste JSON to import", icon = "fa-clipboard", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onPasteJson() }
    Spacer(Modifier.height(8.dp))
    P4Button("Export full app bundle", icon = "fa-box-archive", style = BtnStyle.SUCCESS, modifier = Modifier.fillMaxWidth()) { onExportBundle() }
    Spacer(Modifier.height(8.dp))
    P4Button("Import full app bundle", icon = "fa-box-open", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onImportBundle() }
    Spacer(Modifier.height(8.dp))
    P4Button("Feedback (write a note)", icon = "fa-pen", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) { onFeedback() }
    Spacer(Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        com.peakform.fitness.ui.FaIcon("fa-shield-halved", size = 13.sp, tint = c.ok)
        Spacer(Modifier.width(8.dp))
        Text("Every import asks for a red confirm and takes a guarded snapshot first. Exports import cleanly into the old app.", style = ProType.small, color = c.text3)
    }
}

@Composable
private fun TrainingEnvCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val mode = ProState.data.user.settings.trainingMode
    Text("Controls which exercise library the generator draws from. Switch any time.", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(10.dp))
    listOf(
        Pair("gym", Triple("fa-dumbbell", "Gym", "Full equipment")),
        Pair("bodyweight", Triple("fa-house-chimney", "Home", "No equipment")),
        Pair("mixed", Triple("fa-shuffle", "Mix", "Both libraries")),
    ).forEach { (value, info) ->
        val (icon, t, sub) = info
        val active = mode == value
        Row(
            Modifier.fillMaxWidth().clickable {
                ProState.data = ProState.data.copy(user = ProState.data.user.copy(settings = ProState.data.user.settings.copy(trainingMode = value)))
                ProState.saveWorkoutData()
                onToast("Training environment: $value")
            }.padding(vertical = 5.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (active) c.accentSoft else c.surface2)
                .border(2.dp, if (active) c.accent else c.hairline2, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            com.peakform.fitness.ui.FaIcon(icon, size = 16.sp, tint = if (active) c.accent else c.text2)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(t, style = ProType.label, color = if (active) c.accent else c.text)
                Text(sub, style = ProType.small, color = c.text3)
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun TrainingPrefsCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val user = ProState.data.user
    var height by remember { mutableStateOf(user.height?.toInt()?.toString() ?: "") }
    var exp by remember { mutableStateOf(user.experience.ifBlank { "intermediate" }) }
    var goal by remember { mutableStateOf(user.goal) }
    var rest by remember { mutableStateOf(user.settings.restTime.toString()) }
    var prog by remember { mutableStateOf((user.settings.progressionRate * 100).toString()) }
    // FIX (audit): workoutDays feeds the dashboard weekly goal but had no UI — added a picker.
    var days by remember { mutableStateOf(user.settings.workoutDays.toSet()) }
    val dayNames = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
    // X3/X4: express mode + aggression dial + competition date
    var express by remember { mutableStateOf(user.settings.expressMode) }
    var aggression by remember { mutableStateOf(user.settings.aggression) }
    var competition by remember { mutableStateOf(ProPrefs.get(ctx, "p4_competition") ?: user.competition?.date ?: "") }

    Text("Height (inches)", style = ProType.small, color = c.text3)
    ProTextField(value = height, onValueChange = { height = it.filter { ch -> ch.isDigit() } }, placeholder = "e.g. 70")
    Spacer(Modifier.height(8.dp))
    Text("Experience level", style = ProType.small, color = c.text3)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        listOf("beginner", "intermediate", "advanced").forEach { level ->
            val active = exp == level
            Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (active) c.accentSoft else c.surface2).border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(10.dp)).clickable { exp = level }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text(level.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = if (active) c.accent else c.text2, maxLines = 1)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("Goal", style = ProType.small, color = c.text3)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        listOf("balanced", "hypertrophy", "strength", "endurance", "longevity", "powerlifting").forEach { g ->
            val active = goal == g
            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(if (active) c.accentSoft else c.surface2).border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(999.dp)).clickable { goal = g }.padding(horizontal = 10.dp, vertical = 7.dp)) {
                Text(g.replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = if (active) c.accent else c.text2)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("Preferred workout days (weekly goal)", style = ProType.small, color = c.text3)
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        dayNames.forEach { d ->
            val active = d in days
            val short = d.take(2).replaceFirstChar { it.uppercase() }
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(if (active) c.accentSoft else c.surface2).border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(8.dp)).clickable { days = if (active) days - d else days + d }.padding(horizontal = 9.dp, vertical = 8.dp)) {
                Text(short, fontSize = 11.sp, color = if (active) c.accent else c.text2)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("Rest time between sets (30–300s)", style = ProType.small, color = c.text3)
    ProTextField(value = rest, onValueChange = { rest = it.filter { ch -> ch.isDigit() } }, placeholder = "90")
    Spacer(Modifier.height(8.dp))
    Text("Progression rate (% per week)", style = ProType.small, color = c.text3)
    ProTextField(value = prog, onValueChange = { prog = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "2")
    Spacer(Modifier.height(8.dp))
    ToggleRow("Express mode", "Leaner screens, fewer confirmations", checked = express) { on -> express = on }
    Text("Aggression dial: ${(aggression * 100).toInt()}% — how hard Pro pushes prescriptions.", style = ProType.small, color = c.text3)
    androidx.compose.material3.Slider(
        value = aggression.toFloat(),
        onValueChange = { aggression = it.toDouble() },
        valueRange = 0.6f..1.4f,
        steps = 7,
    )
    Text("Competition date (optional, YYYY-MM-DD)", style = ProType.small, color = c.text3)
    ProTextField(value = competition, onValueChange = { competition = it }, placeholder = "2026-12-01")
    Spacer(Modifier.height(12.dp))
    P4Button("Save preferences", icon = "fa-save", modifier = Modifier.fillMaxWidth()) {
        val s = ProState.data.user.settings
        ProState.data = ProState.data.copy(user = ProState.data.user.copy(
            height = height.toDoubleOrNull()?.coerceIn(36.0, 96.0) ?: user.height,
            experience = exp,
            goal = goal,
            competition = if (competition.isBlank()) user.competition else com.peakform.fitness.core.CompetitionInfo(competition),
            // MERGE semantics — never replaces the settings object (P1 fix)
            settings = s.copy(
                restTime = rest.toIntOrNull()?.coerceIn(30, 300) ?: s.restTime,
                progressionRate = (prog.toDoubleOrNull()?.div(100.0))?.coerceIn(0.005, 0.05) ?: s.progressionRate,
                workoutDays = if (days.isEmpty()) s.workoutDays else days.toList(),
                expressMode = express,
                aggression = aggression.coerceIn(0.6, 1.4),
            ),
        ))
        ProPrefs.put(ctx, "p4_competition", competition)
        ProState.saveWorkoutData()
        onToast("Training preferences saved")
    }
}

@Composable
private fun NotificationsCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    data class NotifOpt(val key: String, val label: String)
    val opts = listOf(
        NotifOpt("retest_reminder", "Show 1RM retest reminders"),
        NotifOpt("deload_notice", "Show deload / taper notices"),
        // FIX (audit): this toggle persisted but nothing could ever read it — the native
        // My Cycle feature is not built yet. Say so instead of implying it works.
        NotifOpt("period_notice", "Show period phase notices (My Cycle — coming soon)"),
    )
    opts.forEach { o ->
        val checked = ProPrefs.get(ctx, "p4_notif_" + o.key) != "false"
        ToggleRow(o.label, checked = checked) { on ->
            ProPrefs.put(ctx, "p4_notif_" + o.key, if (on) "true" else "false")
            onToast(if (on) "Showing ${o.key.replace('_', ' ')}" else "Hiding ${o.key.replace('_', ' ')}")
        }
    }
    Spacer(Modifier.height(6.dp))
    ToggleRow("Deliver as system notifications", "Background reminders via Android (not just in-app)", checked = ProPrefs.get(ctx, "p4_notif_system") != "false") { on ->
        ProPrefs.put(ctx, "p4_notif_system", if (on) "true" else "false")
        onToast(if (on) "System notifications on — reminders arrive even with Pro closed." else "System notifications off.")
    }
    Text("Each reminder also has a \"Don't show today\" option when it appears.", style = ProType.small, color = c.text3)
}

@Composable
private fun ResetFlowDialog(onCancel: () -> Unit, onDone: (String) -> Unit, ctx: Context) {
    val c = LocalProColors.current
    var confirmText by remember { mutableStateOf("") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancel,
        containerColor = c.glass2,
        shape = RoundedCornerShape(28.dp),
        title = { Text("⚠️ Reset All Data", style = ProType.cardTitle, color = c.bad) },
        text = {
            Column {
                Text("This erases ALL workouts, exercises, muscle stats and snapshots. Type RESET to confirm.", style = ProType.body2, color = c.text2)
                Spacer(Modifier.height(10.dp))
                ProTextField(value = confirmText, onValueChange = { confirmText = it }, placeholder = "Type RESET")
            }
        },
        confirmButton = {
            // FIX (audit): wipe ran inside runBlocking on the main thread (ANR on real data);
            // and the 5-step legacy flow's pre-reset backup offer was missing. Both fixed:
            // a last-chance Format A snapshot is written to app files before anything is erased.
            var busy by remember { mutableStateOf(false) }
            P4Button("Erase everything", style = BtnStyle.DANGER, enabled = confirmText == "RESET" && !busy) {
                busy = true
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val backupNote = try {
                        val dir = java.io.File(ctx.filesDir, "backups").apply { mkdirs() }
                        val f = java.io.File(dir, "pre-reset-" + System.currentTimeMillis() + ".json")
                        val json = Backup.exportFormatA(ctx, "pre-reset snapshot")
                        f.writeText(ProJson.pretty.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), json))
                        ProStore(ctx).snapshotVault("pre-reset", ProState.data, ProState.currentWorkout)
                        "All data erased — safety snapshot: files/backups/" + f.name
                    } catch (_: Exception) { "All data erased (snapshot failed)" }
                    try {
                        ProStore(ctx).wipeAllData()
                        // purge legacy mirrors + profile/vault keys (license + registry preserved)
                        val preserve = setOf("deviceId", "p4_theme", "p4_gym", "p4_lang", "p4_vocab", "p4_onboarded", "p4_power_settings", "p4_app_id", "p4_license")
                        ProPrefs.all(ctx).keys.filter { it !in preserve }.forEach { ProPrefs.remove(ctx, it) }
                    } catch (_: Exception) {}
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        ProState.data = WorkoutData()
                        ProState.currentWorkout = null
                        ProState.saveWorkoutData()
                        ProState.notifyChanged()
                        busy = false
                        onDone(backupNote)
                    }
                }
            }
        },
        dismissButton = { P4Button("Cancel", style = BtnStyle.SECONDARY, onClick = onCancel) },
    )
}

@Composable
private fun LegalCenterDialog(onClose: () -> Unit) {
    val c = LocalProColors.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onClose,
        containerColor = c.glass2,
        shape = RoundedCornerShape(22.dp),
        title = { Text("Legal Center", style = ProType.cardTitle, color = c.text) },
        text = {
            Text(
                "The 13-document Legal Center (terms, privacy, safety disclaimers, data policy, medical disclaimer, license) ships with the next update. The app itself collects nothing: no analytics, no ads, no network permissions.",
                style = ProType.body2, color = c.text2,
            )
        },
        confirmButton = { P4Button("Close", onClick = onClose, style = BtnStyle.SECONDARY) },
    )
}


@Composable
fun HealthClearanceCardBody(onToast: (String) -> Unit, onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current

    val tier = remember { Health.tier(ctx) }
    val locked = remember { Health.isGenerationLocked(ctx) }
    val screenedAt = remember { ProPrefs.get(ctx, Health.K_SCREENED) }
    when {
        tier == null -> {
            Text("You haven't run the health screen yet. It takes about a minute.", style = ProType.body2, color = c.text2)
            Spacer(Modifier.height(8.dp))
            P4Button("Run health screen", icon = "fa-notes-medical", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
                onOpenSection("health")
            }
        }
        tier == "C" && locked -> {
            Text("Result: Tier C — Sign-off required.", style = ProType.body2, color = c.bad)
            Text("Workout generation is locked until a doctor clears you. Recording clearance is an honor-system declaration.", style = ProType.small, color = c.text3)
            Spacer(Modifier.height(8.dp))
            P4Button("I have a doctor's clearance — unlock generation", icon = "fa-unlock", style = BtnStyle.DANGER, modifier = Modifier.fillMaxWidth()) {
                Health.confirmClearance(ctx)
                Consent.record(ctx, "health_clearance", "Doctor clearance self-recorded in Settings")
                onToast("Clearance recorded. Workout generation unlocked.")
            }
            Spacer(Modifier.height(6.dp))
            P4Button("Adjust answers & try again", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) {
                onOpenSection("health")
            }
        }
        else -> {
            Text(
                "Result: Tier ${tier} — " + when (tier) {
                    "A" -> "Clear to train."
                    "B" -> "Clear with modification."
                    else -> "Cleared."
                },
                style = ProType.body2, color = if (tier == "A") c.ok else c.warn,
            )
            if (screenedAt != null) Text("Screened ${screenedAt.take(10)}", style = ProType.small, color = c.text3)
            Spacer(Modifier.height(8.dp))
            P4Button("Re-run health screen", icon = "fa-rotate", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) {
                onOpenSection("health")
            }
        }
    }
}

@Composable
fun PrivacyCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    Text("Pro has no internet permission. Your data never leaves this device except when you export it.", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(10.dp))
    ToggleRow("Screenshot-safe mode", "Hide content in screenshots and the recents strip (takes effect next launch)", checked = ProPrefs.get(ctx, "p4_secure") == "on") { on ->
        ProPrefs.put(ctx, "p4_secure", if (on) "on" else "off")
        onToast(if (on) "Screenshot-safe mode on — restart to apply." else "Screenshot-safe mode off.")
    }
    ToggleRow("Predictive back gesture", "Preview the previous screen when swiping back (Android 14+)", checked = true) { _ ->
        onToast("Predictive back is handled by the system.")
    }
}

@Composable
fun AboutCardBody(onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember {
        try {
            val pi = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            "v${pi.versionName} (code ${pi.longVersionCode})"
        } catch (_: Exception) { "unknown" }
    }
    Text("Pro — native fitness tracker", style = ProType.label, color = c.text)
    Text("Version $ver · no analytics · no ads · no network", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(8.dp))
    P4Button("Copy diagnostics", icon = "fa-copy", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) {
        val ring = com.peakform.fitness.ProLog.dumpRing().takeLast(30).joinToString("\n")
        val clip = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clip.setPrimaryClip(android.content.ClipData.newPlainText("pro-diagnostics", "Pro $ver\n$ring"))
        onToast("Last 30 log lines copied")
    }
}
