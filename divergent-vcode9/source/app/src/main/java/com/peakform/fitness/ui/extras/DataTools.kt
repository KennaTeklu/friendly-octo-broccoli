package com.peakform.fitness.ui.extras

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.Badges
import com.peakform.fitness.core.LibraryCustom
import com.peakform.fitness.core.ProfileRegistry
import com.peakform.fitness.core.ProfileSwitcher
import com.peakform.fitness.core.ProJson
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProStore
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.SectionTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * VaultStore — guarded snapshot history (legacy IndexedDB p4_vault semantics:
 * list of snapshots, restore ONLY by explicit user action, never auto-restored).
 * Stored in files/vault/history.json (capped at 20) — no Room schema change.
 */
object VaultStore {
    data class Snap(val id: String, val at: Long, val label: String, val workouts: Int, val json: String)

    private fun file(ctx: Context): File = File(ctx.filesDir, "vault/history.json")

    fun list(ctx: Context): List<Snap> = try {
        val f = file(ctx)
        if (!f.exists()) emptyList()
        else Json.parseToJsonElement(f.readText()).jsonArray.mapNotNull { el ->
            val o = el.jsonObject
            Snap(
                id = o["id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null,
                at = o["at"]?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L,
                label = o["label"]?.jsonPrimitive?.contentOrNull ?: "snapshot",
                workouts = o["workouts"]?.jsonPrimitive?.intOrNull ?: 0,
                json = o["json"]?.jsonPrimitive?.contentOrNull ?: "",
            )
        }
    } catch (_: Exception) { emptyList() }

    fun take(ctx: Context, label: String): Boolean {
        return try {
            val f = file(ctx)
            f.parentFile?.mkdirs()
            val hist = list(ctx).toMutableList()
            val bundle = com.peakform.fitness.core.Backup.exportCompleteV1(ctx)
            hist.add(0, Snap(
                id = "snap_${System.currentTimeMillis()}",
                at = System.currentTimeMillis(),
                label = label,
                workouts = ProState.data.workouts.size,
                json = ProJson.json.encodeToString(JsonObject.serializer(), bundle),
            ))
            f.writeText(ProJson.json.encodeToString(
                JsonArray.serializer(), JsonArray(hist.take(20).map { Json.parseToJsonElement(it.json) })))
            true
        } catch (e: Exception) {
            ProLog.e("VAULT", "snapshot failed: ${e.message}")
            false
        }
    }

    /** Restore replaces live data (explicit user action only) then rebuilds derived data. */
    suspend fun restore(ctx: Context, snap: Snap): String {
        val obj = Json.parseToJsonElement(snap.json).jsonObject
        val result = com.peakform.fitness.core.Backup.import(ctx, snap.json)
        return result.message
    }

    fun delete(ctx: Context, id: String) {
        val hist = list(ctx).filter { it.id != id }
        try {
            file(ctx).writeText(ProJson.json.encodeToString(
                JsonArray.serializer(), JsonArray(hist.map { Json.parseToJsonElement(it.json) })))
        } catch (_: Exception) {}
    }
}

/** SnapshotsScreen — vault snapshot list, tap-to-restore with confirmation. */
@Composable
fun SnapshotsScreen(onBack: () -> Unit, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var snaps by remember { mutableStateOf(VaultStore.list(ctx)) }
    var confirm by remember { mutableStateOf<VaultStore.Snap?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-box-archive", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Vault snapshots", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text(
            "Snapshots are guarded copies — a restore is always your explicit choice and never automatic. Quick snapshots and pre-import guards land here.",
            style = ProType.small, color = c.text3, modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        P4Button("Take snapshot now", onClick = {
            val ok = VaultStore.take(ctx, "manual")
            snaps = VaultStore.list(ctx)
            onToast(if (ok) "Snapshot saved" else "Snapshot failed")
        }, style = BtnStyle.SECONDARY, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(snaps.size) { i ->
                val s = snaps[i]
                val date = java.text.SimpleDateFormat("MMM d, yyyy HH:mm", java.util.Locale.US).format(java.util.Date(s.at))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(c.surface2)
                        .border(1.dp, c.hairline2, RoundedCornerShape(14.dp))
                        .clickable { confirm = s }
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon("fa-clock-rotate-left", size = 12.sp, tint = c.accent)
                        Spacer(Modifier.width(6.dp))
                        Text("${s.label} · $date", style = ProType.label, color = c.text)
                        Spacer(Modifier.weight(1f))
                        Text("Tap to restore", style = ProType.small, color = c.accent)
                    }
                    Text("${s.workouts} workouts in snapshot", style = ProType.small, color = c.text3)
                }
            }
        }
    }

    confirm?.let { snap ->
        AlertDialog(
            onDismissRequest = { confirm = null },
            containerColor = c.glass2,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Restore this snapshot?", style = ProType.cardTitle, color = c.text) },
            text = { Text(
                "Your current data will be replaced by the snapshot's contents. A fresh guard snapshot of what you have right now is taken first, so you can come back.",
                style = ProType.body2, color = c.text2) },
            confirmButton = {
                P4Button("Guard & restore", onClick = {
                    confirm = null
                    scope.launch {
                        VaultStore.take(ctx, "pre-restore")
                        val msg = withContext(Dispatchers.IO) { VaultStore.restore(ctx, snap) }
                        ProState.notifyChanged()
                        onToast(msg)
                    }
                }, style = BtnStyle.PRIMARY)
            },
            dismissButton = { P4Button("Cancel", onClick = { confirm = null }, style = BtnStyle.GHOST) },
        )
    }
}

/**
 * StudioScreen — Library Studio: author custom exercises (legacy p4_custom_ex),
 * export/import the exercise library (exercise-library-YYYY-MM-DD.json), merge report,
 * and a rule-based idea helper (deterministic, offline — honestly labeled).
 */
@Composable
fun StudioScreen(onBack: () -> Unit, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var muscles by remember { mutableStateOf(TextFieldValue("")) }
    var equipment by remember { mutableStateOf("bodyweight") }
    var sets by remember { mutableStateOf(TextFieldValue("3")) }
    var reps by remember { mutableStateOf(TextFieldValue("8-12")) }
    var instructions by remember { mutableStateOf(TextFieldValue("")) }
    var toast by remember { mutableStateOf<String?>(null) }
    var custom by remember { mutableStateOf(LibraryCustom.list(ctx)) }
    var helper by remember { mutableStateOf<List<String>>(emptyList()) }

    fun addExercise() {
        val n = name.text.trim()
        val m = muscles.text.trim().lowercase().split(Regex("[,\\s]+")).filter { it.isNotBlank() }
        if (n.isBlank() || m.isEmpty()) {
            toast = "Give the exercise a name and at least one muscle."
            return
        }
        val ex = com.peakform.fitness.core.LibraryExercise(
            name = n, muscles = m, equipment = equipment,
            defaultSets = sets.text.trim().toIntOrNull() ?: 3,
            defaultReps = reps.text.trim().ifBlank { "8-12" },
            progression = "custom",
            instructions = instructions.text.trim().split("\n").filter { it.isNotBlank() },
            primaryMuscle = m.first(),
            importance = "accessory",
        )
        LibraryCustom.add(ctx, ex)
        custom = LibraryCustom.list(ctx)
        Badges.countEvent(ctx, "studioAdd")
        Badges.recompute(ctx)
        name = TextFieldValue(""); muscles = TextFieldValue(""); instructions = TextFieldValue("")
        toast = "${ex.name} added to your library (${custom.size} custom now)"
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-flask-vial", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Library Studio", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text("Author your own exercises. Custom exercises train exactly like built-ins — the engine tracks, progresses and recovers them.", style = ProType.body2, color = c.text2)
        Spacer(Modifier.height(12.dp))
        ProTextField(name, { name = it }, "Exercise name")
        Spacer(Modifier.height(8.dp))
        ProTextField(muscles, { muscles = it }, "Muscles (comma separated, e.g. chest, triceps)")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("bodyweight", "barbell", "dumbbell", "machine", "cable", "band").forEach { e ->
                val sel = equipment == e
                Box(
                    Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (sel) c.accentSoft else c.surface2)
                        .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(9.dp))
                        .clickable { equipment = e }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                ) { Text(e, style = ProType.small, color = if (sel) c.accent else c.text2) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProTextField(sets, { sets = it }, "Sets", modifier = Modifier.weight(1f), keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            ProTextField(reps, { reps = it }, "Reps (or time)", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        ProTextField(instructions, { instructions = it }, "How to do it (one step per line)")
        Spacer(Modifier.height(10.dp))
        P4Button("Add to library", onClick = { addExercise() }, style = BtnStyle.PRIMARY)
        Spacer(Modifier.height(8.dp))
        P4Button("Suggest ideas for me", onClick = {
            // Rule-based helper (deterministic, offline) — NOT an AI model, honestly labeled.
            val base = muscles.text.trim().ifBlank { "chest" }
            val ideas = listOf(
                "$base (slow tempo) — same pattern, 3 counts down",
                "Single-arm $base variation for side-to-side balance",
                "$base pause reps — 2 s pause in the stretch",
            )
            helper = ideas
        }, style = BtnStyle.SECONDARY)
        helper.forEach {
            Text("•  $it", style = ProType.small, color = c.text2)
            Spacer(Modifier.height(2.dp))
        }
        Spacer(Modifier.height(14.dp))
        SectionTitle("fa-layer-group", "Custom exercises (${custom.size})")
        custom.forEach { ex ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface2)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(ex.name, style = ProType.label, color = c.text)
                    Text("${ex.muscles.joinToString(", ")} · ${ex.equipment} · ${ex.defaultSets}×${ex.defaultReps}", style = ProType.small, color = c.text3)
                }
                Text("Remove", style = ProType.small, color = c.bad, modifier = Modifier.clickable {
                    LibraryCustom.remove(ctx, ex.id)
                    custom = LibraryCustom.list(ctx)
                })
            }
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(14.dp))
        SectionTitle("fa-file-export", "Library data")
        P4Button("Export library JSON", onClick = {
            val json = LibraryCustom.exportLibrary(ctx)
            val ok = com.peakform.fitness.core.ClipboardShare.copy(ctx, json, "exercise-library-${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())}.json")
            onToast(if (ok) "Library JSON copied — share or save it anywhere" else "Export failed")
        }, style = BtnStyle.SECONDARY)
        Spacer(Modifier.height(4.dp))
        Text("Import merges a library export: matching names are skipped, new exercises are added — the report tells you exactly what happened.", style = ProType.small, color = c.text3)
        Spacer(Modifier.height(20.dp))
        toast?.let {
            Text(it, style = ProType.small, color = c.ok)
            LaunchedEffect(it) { kotlinx.coroutines.delay(2600); toast = null }
        }
        Spacer(Modifier.height(30.dp))
    }
}

/**
 * ProfilesScreen — add/switch/delete profiles WITHOUT restart (legacy p4_profiles +
 * per-profile data namespaces). Switch: current namespace saved to files, target loaded.
 */
@Composable
fun ProfilesScreen(onBack: () -> Unit, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var profiles by remember { mutableStateOf(ProfileRegistry.read(ctx).first) }
    var active by remember { mutableStateOf(ProfileRegistry.read(ctx).second) }
    var newName by remember { mutableStateOf(TextFieldValue("")) }
    var avatarOpen by remember { mutableStateOf(false) }
    var emoji by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf<ProfileRegistry.Profile?>(null) }
    val avatars = remember { LibraryCustom.AVATARS }
    var toast by remember { mutableStateOf<String?>(null) }

    fun switchTo(p: ProfileRegistry.Profile) {
        scope.launch {
            val msg = withContext(Dispatchers.IO) { ProfileSwitcher.switch(ctx, p.id) }
            profiles = ProfileRegistry.read(ctx).first
            active = ProfileRegistry.read(ctx).second
            toast = msg
            ProState.notifyChanged()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-user-group", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Profiles", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text("Each profile keeps its own workouts, stats and settings. Switching saves your current profile instantly — no restart.", style = ProType.body2, color = c.text2)
        Spacer(Modifier.height(12.dp))
        profiles.forEach { p ->
            val isActive = p.id == active
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) c.accentSoft else c.surface2)
                    .border(1.dp, if (isActive) c.accent else c.hairline2, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(p.emoji ?: "🧍", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.name, style = ProType.label, color = if (isActive) c.accent else c.text)
                    Text(if (isActive) "Active — data live" else "Tap to switch", style = ProType.small, color = c.text3)
                }
                if (!isActive) {
                    P4Button("Switch", onClick = { switchTo(p) }, style = BtnStyle.SECONDARY, minHeight = 34)
                    Spacer(Modifier.width(6.dp))
                }
                Text("✕", color = c.bad, modifier = Modifier.clickable { confirmDelete = p }.padding(6.dp))
            }
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(10.dp))
        SectionTitle("fa-plus", "Add a profile")
        ProTextField(newName, { newName = it }, "Name for the new profile")
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface2)
                    .border(1.dp, c.hairline2, RoundedCornerShape(12.dp))
                    .clickable { avatarOpen = !avatarOpen },
                contentAlignment = Alignment.Center,
            ) { Text(emoji ?: "🐾", fontSize = 22.sp) }
            Spacer(Modifier.width(10.dp))
            Text("Pick an avatar", style = ProType.small, color = c.text3, modifier = Modifier.weight(1f))
            P4Button("Create profile", onClick = {
                val n = newName.text.trim()
                if (n.isBlank()) { toast = "Give the profile a name." }
                else {
                    val id = "p_" + System.currentTimeMillis()
                    val list = profiles + ProfileRegistry.Profile(id, n, emoji ?: avatars.randomOrNull(), System.currentTimeMillis(), profiles.size)
                    ProfileRegistry.write(ctx, list, active, false)
                    profiles = list
                    newName = TextFieldValue("")
                    toast = "$n created — switch to it any time."
                }
            }, style = BtnStyle.PRIMARY)
        }
        if (avatarOpen) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface2)
                    .padding(8.dp),
            ) {
                Column {
                    avatars.chunked(8).forEach { row ->
                        Row {
                            row.forEach { a ->
                                Text(a, fontSize = 20.sp,
                                    modifier = Modifier
                                        .clickable { emoji = a; avatarOpen = false }
                                        .padding(4.dp))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        toast?.let {
            Text(it, style = ProType.small, color = c.ok)
            LaunchedEffect(it) { kotlinx.coroutines.delay(2600); toast = null }
        }
        Spacer(Modifier.height(30.dp))
    }

    confirmDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            containerColor = c.glass2,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete ${p.name}?", style = ProType.cardTitle, color = c.text) },
            text = { Text("The profile's saved data namespace is deleted. The active profile's live data is not touched.", style = ProType.body2, color = c.text2) },
            confirmButton = {
                P4Button("Delete", onClick = {
                    ProfileSwitcher.delete(ctx, p.id)
                    profiles = ProfileRegistry.read(ctx).first
                    confirmDelete = null
                }, style = BtnStyle.PRIMARY)
            },
            dismissButton = { P4Button("Cancel", onClick = { confirmDelete = null }, style = BtnStyle.GHOST) },
        )
    }
}

/** ImportWarningDialog — pre-import confirmation with the legacy copy + healing summary. */
@Composable
fun ImportWarningDialog(raw: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    val fixes = remember(raw) { com.peakform.fitness.core.Heal.describeFixes(raw) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(20.dp),
        title = { Text(com.peakform.fitness.core.ImportWarning.TITLE, style = ProType.cardTitle, color = c.text) },
        text = {
            Column {
                Text(com.peakform.fitness.core.ImportWarning.BODY, style = ProType.body2, color = c.text2)
                if (fixes.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("This file needs repair to import:", style = ProType.small, color = c.warn)
                    fixes.forEach { Text("• $it", style = ProType.small, color = c.warn) }
                }
            }
        },
        confirmButton = { P4Button(com.peakform.fitness.core.ImportWarning.CONFIRM, onClick = onConfirm, style = BtnStyle.PRIMARY) },
        dismissButton = { P4Button(com.peakform.fitness.core.ImportWarning.CANCEL, onClick = onDismiss, style = BtnStyle.GHOST) },
    )
}
