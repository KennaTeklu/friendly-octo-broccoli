package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * ProfilesScreen — picker grid with emoji, add (random animal emoji), delete with
 * legacy confirm copy, and LIVE switching without restart (Profiles.switchTo).
 */
@Composable
fun ProfilesScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var ver by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var toast by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf<Profiles.ProfileMeta?>(null) }

    val profiles = remember(ver) { Profiles.list(ctx) }
    val activeId = remember(ver) { Profiles.activeId(ctx) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-user-group", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Text("Profiles", style = ProType.brand, color = c.text, modifier = Modifier.weight(1f))
            Text("close", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onClose() })
        }
        Text("Each profile keeps its own data namespace. Switching is instant.", style = ProType.small, color = c.text3)

        profiles.forEach { p ->
            val active = p.id == activeId
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (active) c.accentSoft else c.surface)
                    .border(1.dp, if (active) c.accentLine else c.hairline, RoundedCornerShape(14.dp))
                    .clickable {
                        if (!active) {
                            val name = Profiles.switchTo(ctx, p.id)
                            ver++
                            toast = "Switched to $name"
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(p.emoji ?: "🏋️", fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.name, style = ProType.label, color = if (active) c.accent else c.text)
                    Text(if (active) "CURRENT" else "Tap to switch", style = ProType.small, color = c.text3)
                }
                if (p.id != Profiles.DEFAULT) {
                    Text("delete", style = ProType.small, color = c.bad, modifier = Modifier.clickable { confirmDelete = p })
                }
            }
        }

        if (showAdd) {
            ProTextField(value = newName, onValueChange = { newName = it.take(30) }, placeholder = "New profile name")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                P4Button("Cancel", style = BtnStyle.GHOST, onClick = { showAdd = false })
                P4Button("Create", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f), enabled = newName.isNotBlank()) {
                    val p = Profiles.create(ctx, newName.trim())
                    newName = ""
                    showAdd = false
                    ver++
                    toast = "Profile ${p.name} created (${p.emoji})"
                }
            }
        } else {
            P4Button("Add profile", icon = "fa-plus", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth(), onClick = { showAdd = true })
        }
        Spacer(Modifier.height(110.dp))

        confirmDelete?.let { p ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { confirmDelete = null },
                containerColor = c.glass2,
                shape = RoundedCornerShape(22.dp),
                title = { Text("Delete ${p.name}?", style = ProType.cardTitle, color = c.text) },
                text = { Text("Delete this profile and all its data?", style = ProType.body2, color = c.text2) },
                confirmButton = {
                    P4Button("Delete", style = BtnStyle.DANGER, onClick = {
                        Profiles.delete(ctx, p.id)
                        confirmDelete = null
                        ver++
                        toast = "Profile deleted"
                    })
                },
                dismissButton = { P4Button("Cancel", style = BtnStyle.SECONDARY, onClick = { confirmDelete = null }) },
            )
        }
        toast?.let { t ->
            LaunchedEffect(t) { kotlinx.coroutines.delay(2400); toast = null }
            ToastBanner(t)
        }
    }
}
