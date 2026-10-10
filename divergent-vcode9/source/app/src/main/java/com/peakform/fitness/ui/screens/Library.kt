package com.peakform.fitness.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.Favorites
import com.peakform.fitness.core.LibraryExercise
import com.peakform.fitness.core.PlateMath
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Library — v1.4.0. The legacy #library-section, completed and elevated:
 *  • ALL 6 legacy grouping modes (HTML expert: 6700-6705 — muscleGroup / equipment /
 *    difficulty / performed / muscleCategory / component) + 2 native (Pattern, Starred).
 *  • `p4_library_collapsed` persistence — same localStorage key, undefined ⇒ collapsed
 *    (legacy rule 60084). Expand All / Collapse All mutually exclusive pair (6707-6710).
 *  • Component filter chips with counts (mpRenderLibraryChips 48456-48479).
 *  • Cards now carry MEDIA: animated RigArt pictograms (the legacy library was text-only),
 *    the legacy badge set (Needs 1RM 59893, ✔ Done Nx 59984, Easy/Medium/Hard 59986,
 *    🏆 Tested 1RM 59983, Last 59992, ↑ Next 60003), and an inline expand — collapsed by
 *    default, one card open at a time (legacy one-open semantics).
 *  • 1RM Lab button on every card (per-exercise test + input, persisted).
 *  • LazyColumn stream (legacy streamed cards in rAF chunks of 20 — native virtualization
 *    replaces chunking), staggered entrance, haptics (UI expert).
 */

private val GROUPINGS = listOf(
    "muscleGroup" to "Muscle", "equipment" to "Equipment", "difficulty" to "Difficulty",
    "performed" to "Performed", "muscleCategory" to "Category", "component" to "Component",
    "pattern" to "Pattern", "favorites" to "Starred",
)

private val COLLAPSE_KEY = "p4_library_collapsed"
private val collapseJson = Json { ignoreUnknownKeys = true }

private fun loadCollapsed(): MutableMap<String, Boolean> =
    ProPrefs.get(Fa.appContext!!, COLLAPSE_KEY)?.let { raw ->
        runCatching {
            collapseJson.decodeFromString(MapSerializer(String.serializer(), kotlin.Boolean.serializer()), raw).toMutableMap()
        }.getOrNull()
    } ?: mutableMapOf()

private fun saveCollapsed(m: Map<String, Boolean>) {
    ProPrefs.put(Fa.appContext!!, COLLAPSE_KEY, collapseJson.encodeToString(MapSerializer(String.serializer(), kotlin.Boolean.serializer()), m))
}

private fun pretty(s: String) = s.replace('_', ' ').replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(onAddToWorkout: (LibraryExercise) -> Unit, onOpenWiki: (String) -> Unit = {}, onToast: (String) -> Unit = {}) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val view = LocalView.current
    val tick = { view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) }
    val confirmHaptic = {
        view.performHapticFeedback(
            if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.VIRTUAL_KEY,
        )
    }

    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val _verTick = ver.intValue

    var search by remember { mutableStateOf("") }
    var groupBy by remember { mutableIntStateOf(0) }
    var componentFilter by remember { mutableStateOf<String?>(null) }
    var openDetail by remember { mutableStateOf<LibraryExercise?>(null) }
    var openLab by remember { mutableStateOf<LibraryExercise?>(null) }
    var openCardId by remember { mutableStateOf<String?>(null) }
    var collapsed by remember { mutableStateOf(loadCollapsed()) }
    var favs by remember { mutableStateOf(Favorites.load(ctx)) }

    val mode = ProState.data.user.settings.trainingMode
    val all = remember(ver.intValue, mode) { Library.allLibraryExercises(mode).map { Library.augmented(it) } }

    // v1.3.0 synonym expansion (legacy 65061) + fuzzy rank
    val filtered = remember(search, all, componentFilter, favs, groupBy) {
        var list = all
        if (componentFilter != null) {
            list = list.filter { ex -> Library.componentFor(ex.id).firstOrNull() == componentFilter }
        }
        if (groupBy == 7) list = list.filter { ex -> ex.id in favs }
        if (search.length < 2) list
        else {
            val terms = com.peakform.fitness.core.Synonyms.expand(search)
            list.map { ex -> ex to com.peakform.fitness.core.Synonyms.score(ex.name, terms) }
                .filter { (ex, score) ->
                    score > 0 ||
                        ex.muscles.any { it.lowercase().contains(search.lowercase()) } ||
                        ex.equipment.lowercase().contains(search.lowercase())
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }
    }

    val grouped: List<Pair<String, List<LibraryExercise>>> = remember(filtered, groupBy) {
        when (GROUPINGS[groupBy].first) {
            "equipment" -> filtered.groupBy { it.equipment }.toSortedMap().map { (k, v) -> pretty(k) to v }
            "difficulty" -> {
                fun diff(sf: Double) = if (sf < 0.5) "Hard" else if (sf < 0.7) "Medium" else "Easy"
                listOf("Hard", "Medium", "Easy").mapNotNull { label ->
                    val list = filtered.filter { diff(it.skillFactor) == label }
                    if (list.isEmpty()) null else label to list
                }
            }
            // legacy 60049-60050: performed → 'Done'/'Not Done', Not Done FIRST
            "performed" -> {
                val done = filtered.filter { ProState.data.exercises[it.id]?.history?.isNotEmpty() == true }
                val not = filtered.filter { ProState.data.exercises[it.id]?.history?.isNotEmpty() != true }
                listOfNotNull(
                    if (not.isEmpty()) null else "Not Done" to not,
                    if (done.isEmpty()) null else "Done" to done,
                )
            }
            // legacy 60051-60053: muscle category (Major/Longevity/Hands/Feet/other)
            "muscleCategory" -> filtered.groupBy { ex ->
                Library.muscleDef(ex.primaryMuscle.ifBlank { ex.muscles.firstOrNull() ?: "" })?.category ?: "other"
            }.toList().sortedBy { it.first }.map { (k, v) -> pretty(k) to v }
            // legacy 60054-60056: first fitness component's short name
            "component" -> filtered.groupBy { ex ->
                val cid = Library.componentFor(ex.id).firstOrNull()
                Library.mpComponents.firstOrNull { it.id == cid }?.short ?: "Other"
            }.toList().sortedBy { it.first }.map { (k, v) -> k to v }
            "pattern" -> filtered.groupBy { ex -> PATTERN_LABELS[patternFor(ex)] ?: "Other" }
                .toList().sortedByDescending { it.second.size }.map { (k, v) -> k to v }
            "favorites" -> if (filtered.isEmpty()) emptyList() else listOf("Starred" to filtered)
            else -> filtered.groupBy { it.primaryMuscle.ifBlank { "other" } }
                .toList()
                .sortedByDescending { it.second.size }
                .map { (k, v) -> pretty(k) to v }
        }
    }

    fun setCollapsed(key: String, value: Boolean) {
        collapsed[key] = value
        saveCollapsed(collapsed)
    }

    // component chip counts (legacy mpRenderLibraryChips)
    val componentCounts = remember(filtered) {
        filtered.groupBy { Library.componentFor(it.id).firstOrNull() }
            .mapNotNull { (cid, list) ->
                val comp = Library.mpComponents.firstOrNull { it.id == cid } ?: return@mapNotNull null
                Triple(comp, list.size, cid)
            }
            .sortedByDescending { it.second }
    }

    val anyCollapsed = grouped.any { collapsed[it.first] != false }
    val anyExpanded = grouped.any { collapsed[it.first] == false }

    // shared rig clock — one infinite transition drives every card pictogram (UI expert 1/7)
    val rigClock = rememberRigClock()

    CompositionLocalProvider(LocalRigClock provides rigClock) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column {
                Spacer(Modifier.height(4.dp))
                SectionTitle("fa-book-open", "Exercise Library")
                Text(
                    "${rememberCountUp(all.size, 800)} Movements Available",
                    style = ProType.small, color = c.text3,
                )
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // legacy 6707-6710: mutually exclusive Expand/Collapse All
                if (anyCollapsed) {
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp)).background(c.accentSoft)
                            .clickable {
                                grouped.forEach { collapsed[it.first] = false }
                                saveCollapsed(collapsed); tick()
                            }.padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text("Expand All", fontSize = 11.sp, color = c.accent, fontWeight = FontWeight.SemiBold)
                    }
                } else if (anyExpanded) {
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp)).background(c.surface2).border(1.dp, c.hairline2, RoundedCornerShape(10.dp))
                            .clickable {
                                grouped.forEach { collapsed[it.first] = true }
                                saveCollapsed(collapsed); tick()
                            }.padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text("Collapse All", fontSize = 11.sp, color = c.text2, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text("${filtered.size} shown", style = ProType.small, color = c.text3)
            }
        }
        item {
            ProTextField(value = search, onValueChange = { search = it }, placeholder = "Search exercises or muscles...")
        }
        item {
            // grouping modes — 6 legacy + 2 native, horizontally scrollable pills
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GROUPINGS.forEachIndexed { i, (_, label) ->
                    val active = groupBy == i
                    Box(
                        Modifier.clip(RoundedCornerShape(999.dp))
                            .background(if (active) c.accentSoft else c.surface2)
                            .border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(999.dp))
                            .clickable { groupBy = i; componentFilter = null; collapsed = loadCollapsed(); tick() }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(label, fontSize = 12.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        if (componentCounts.isNotEmpty()) {
            item {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Chip("All ${filtered.size}", componentFilter == null, c.text) { componentFilter = null }
                    componentCounts.forEach { (comp, count, cid) ->
                        Chip("${comp.short} $count", componentFilter == cid, runCatching { Color(android.graphics.Color.parseColor(comp.color)) }.getOrDefault(c.accent)) {
                            componentFilter = if (componentFilter == cid) null else cid
                        }
                    }
                }
            }
        }

        grouped.forEach { (group, list) ->
            val isCollapsed = collapsed[group] != false
            item(key = "h_$group") {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.bg.copy(alpha = 0.92f))
                        .clickable { setCollapsed(group, !isCollapsed); tick() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(group, style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
                    Text("(${list.size})", style = ProType.small, color = c.text3)
                    Spacer(Modifier.width(8.dp))
                    FaIcon(if (isCollapsed) "fa-chevron-down" else "fa-chevron-up", size = 12.sp, tint = c.accent)
                }
            }
            if (!isCollapsed) {
                val cap = if (search.isNotBlank()) 200 else if (group in showMoreGroups.value) list.size else 60
                items(list.take(cap), key = { "${group}_${it.id}" }) { ex ->
                    LibraryCard(
                        ex = ex,
                        expanded = openCardId == ex.id,
                        starred = ex.id in favs,
                        onToggleStar = { favs = Favorites.toggle(ctx, ex.id); tick() },
                        onToggleExpand = { openCardId = if (openCardId == ex.id) null else ex.id; tick() },
                        onAdd = {
                            confirmHaptic()
                            // toast + navigation handled by MainActivity's onAddToWorkout
                            onAddToWorkout(ex)
                        },
                        onLab = { openLab = ex },
                        onDetail = { openDetail = ex },
                        onOpenWiki = onOpenWiki,
                    )
                }
                if (list.size > cap) {
                    item(key = "more_$group") {
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.surface2)
                                .clickable { showMoreGroups.value = showMoreGroups.value + group }.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Show more (${list.size - cap} more)", style = ProType.small, color = c.accent)
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyState("fa-magnifying-glass", "No exercises match your search", "Try a different term or clear the filter.")
            }
        }
        item { Spacer(Modifier.height(120.dp)) }
    }
    }

    openDetail?.let { ex ->
        ExerciseDetailSheet(
            exercise = ex,
            onClose = { openDetail = null },
            onAdd = { onAddToWorkout(ex); openDetail = null },
            onLab = { openLab = ex; openDetail = null },
        )
    }
    openLab?.let { ex ->
        com.peakform.fitness.ui.extras.OneRmLabSheet(exercise = ex, onDismiss = { openLab = null }, onToast = onToast)
    }
}

private val showMoreGroups = mutableStateOf(setOf<String>())

@Composable
private fun Chip(label: String, active: Boolean, tint: Color, onClick: () -> Unit) {
    val c = LocalProColors.current
    Box(
        Modifier.clip(RoundedCornerShape(999.dp))
            .background(if (active) c.accentSoft else c.surface2)
            .border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(label, fontSize = 11.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold)
    }
}

/** One exercise card — collapsed compact row (media + badges) that morphs open inline. */
@Composable
fun LibraryCard(
    ex: LibraryExercise,
    expanded: Boolean,
    starred: Boolean,
    onToggleStar: () -> Unit,
    onToggleExpand: () -> Unit,
    onAdd: () -> Unit,
    onLab: () -> Unit,
    onDetail: () -> Unit = {},
    onOpenWiki: (String) -> Unit = {},
) {
    val c = LocalProColors.current
    val rec = ProState.data.exercises[ex.id]
    val difficulty = if (ex.skillFactor < 0.5) "Hard" else if (ex.skillFactor < 0.7) "Medium" else "Easy"
    val diffColor = when (difficulty) {
        "Hard" -> c.bad
        "Medium" -> c.warn
        else -> c.ok
    }
    val hue = muscleHue(ex.muscles)
    val pattern = remember(ex.id) { patternFor(ex) }
    val chevRot by animateFloatAsState(if (expanded) 180f else 0f, spring(dampingRatio = 0.7f, stiffness = 500f), label = "chev")
    val doneCount = rec?.history?.count { !it.isTest } ?: 0
    val tested = rec?.tested1RM
    val mu = rec?.mu

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.surface2)
            .border(1.dp, if (expanded) c.accentLine else c.hairline, RoundedCornerShape(16.dp))
            .animateContentSize(spring(dampingRatio = 0.85f, stiffness = 380f))
            .clickable { onToggleExpand() },
    ) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            // media thumbnail + difficulty arc (UI expert 12)
            Box(
                Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.surface3),
                contentAlignment = Alignment.Center,
            ) {
                RigArt(pattern = pattern, equipment = ex.equipment, hue = hue, seed = ex.id.hashCode(), size = 78.dp)
                androidx.compose.foundation.Canvas(Modifier.size(84.dp)) {
                    drawArc(
                        c.hairline2, -90f, 360f, false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                    drawArc(
                        diffColor, -90f, (1f - ex.skillFactor.toFloat()).coerceIn(0.08f, 1f) * 360f, false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                }
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ex.name, style = ProType.label, color = c.text, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 2)
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp)).clickable { onToggleStar() }.padding(4.dp),
                    ) {
                        FaIcon(if (starred) "fa-star" else "fa-star-sharp", size = 14.sp, tint = if (starred) Color(0xFFFBBF24) else c.text3, contentDescription = "favorite")
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    // legacy badges 59893/59984
                    if (tested == null && (mu == null || mu <= 0)) Badge("Needs 1RM", c.warn)
                    if (doneCount > 0) Badge("✔ Done ${doneCount}x", c.ok) else Badge("New", c.text3)
                    Badge(difficulty, diffColor)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ex.muscles.take(2).forEach { m ->
                        Box(
                            Modifier.clip(RoundedCornerShape(999.dp)).background(hue.copy(alpha = 0.14f))
                                .clickable { onOpenWiki(m) }
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(m.replace('_', ' '), fontSize = 10.sp, color = hue)
                        }
                    }
                    if (ex.muscles.size > 2) Text("+${ex.muscles.size - 2}", fontSize = 10.sp, color = c.text3)
                }
                Spacer(Modifier.height(4.dp))
                Text("${ex.defaultSets} × ${ex.defaultReps} · ${ex.equipment.replaceFirstChar { it.uppercase() }}", style = ProType.small, color = c.text3)
                // 1RM line: legacy 🏆 Tested (59983) / Est. (60019) + Last (59992) / ↑ Next (60003)
                if (tested != null && tested > 0) {
                    Text("🏆 Tested 1RM: ${PlateMath.fmt(tested)} lbs", style = ProType.small, color = Color(0xFFFBBF24))
                } else if (mu != null && mu > 0) {
                    Text("Est. 1RM: ${rememberCountUp(mu.toInt())} lbs", style = ProType.small, color = c.accent)
                }
                val last = rec?.history?.lastOrNull { !it.skipped }
                val next = rec?.nextWeight
                if (last != null || next != null) {
                    Text(
                        buildString {
                            append("Last: ")
                            append(if ((last?.weight ?: 0.0) > 0) "${PlateMath.fmt(last!!.weight)} lbs" else "bodyweight")
                            if (next != null && next > 0) append("  ·  ↑ Next: ${PlateMath.fmt(next)}")
                        },
                        style = ProType.small, fontSize = 10.sp, color = c.text3,
                    )
                }
            }
            FaIcon(if (expanded) "fa-chevron-up" else "fa-chevron-down", size = 12.sp, tint = c.text3, modifier = Modifier.graphicsLayer { rotationZ = chevRot })
        }

        AnimatedVisibility(visible = expanded) {
            Column(Modifier.padding(horizontal = 11.dp).padding(bottom = 11.dp)) {
                // big media banner — square rig centered, surface backdrop
                Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)).background(c.surface3)) {
                    RigArt(pattern = pattern, equipment = ex.equipment, hue = hue, seed = ex.id.hashCode(), size = 150.dp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.height(8.dp))
                if (ex.instructions.isNotEmpty()) {
                    ex.instructions.take(2).forEachIndexed { i, s ->
                        Text("${i + 1}. ${s.replace(Regex("<[^>]*>"), "")}", style = ProType.small, color = c.text2, maxLines = 2)
                    }
                }
                rec?.workingWeightNote?.let {
                    Spacer(Modifier.height(4.dp))
                    Text("Note: $it", style = ProType.small, fontSize = 10.sp, color = c.text3, maxLines = 1)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    P4Button("Add to Workout", icon = "fa-plus", style = BtnStyle.INFO, minHeight = 36, modifier = Modifier.weight(1f)) { onAdd() }
                    P4Button("1RM Lab", icon = "fa-dumbbell", style = BtnStyle.PRIMARY, minHeight = 36, modifier = Modifier.weight(1f)) { onLab() }
                }
                Spacer(Modifier.height(6.dp))
                P4Button("How to do it", icon = "fa-book-open", style = BtnStyle.SECONDARY, minHeight = 36, modifier = Modifier.fillMaxWidth()) { onDetail() }
            }
        }
    }
}

@Composable
private fun Badge(text: String, tint: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(999.dp)).background(tint.copy(alpha = 0.14f)).padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(text, fontSize = 9.sp, color = tint, fontWeight = FontWeight.Bold)
    }
}

/** Detail sheet — now with the animated demo banner, fiber-profile bars and 1RM Lab link. */
@Composable
fun ExerciseDetailSheet(
    exercise: LibraryExercise,
    onClose: () -> Unit,
    onAdd: () -> Unit,
    onLab: () -> Unit = {},
) {
    val c = LocalProColors.current
    val hue = muscleHue(exercise.muscles)
    val pattern = remember(exercise.id) { patternFor(exercise) }
    val fiber = remember(exercise.id) { Library.fiberWindowFor(exercise.muscles) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onClose,
        containerColor = c.glass2,
        shape = RoundedCornerShape(22.dp),
        title = { Text(exercise.name, style = ProType.cardTitle, color = c.text) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Box(Modifier.fillMaxWidth().height(132.dp).clip(RoundedCornerShape(16.dp)).background(c.surface3)) {
                    RigArt(pattern = pattern, equipment = exercise.equipment, hue = hue, seed = exercise.id.hashCode(), size = 132.dp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.height(10.dp))
                Text("Muscles: ${exercise.muscles.joinToString(", ") { it.replace('_', ' ') }}", style = ProType.small, color = c.text2)
                Text("Equipment: ${exercise.equipment} · Sets: ${exercise.defaultSets} × ${exercise.defaultReps}", style = ProType.small, color = c.text2)
                if (exercise.progression.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Progression: ${exercise.progression}", style = ProType.small, color = c.text3)
                }
                // Muscle Fiber Profile (legacy detail modal 48526-48610)
                Spacer(Modifier.height(10.dp))
                Text("Muscle Fiber Profile — ${fiber.fiber}", style = ProType.label, color = c.text)
                Text(
                    "Target intensity window ${fiber.min.toInt()}–${fiber.max.toInt()}% of 1RM · ${fiber.note}",
                    style = ProType.small, fontSize = 10.sp, color = c.text3,
                )
                Spacer(Modifier.height(4.dp))
                Canvas(Modifier.fillMaxWidth().height(10.dp)) {
                    drawRoundRect(
                        c.surface3,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
                    )
                    val x0 = size.width * (fiber.min / 100.0).toFloat()
                    val x1 = size.width * (fiber.max / 100.0).toFloat()
                    if (x1 > x0) {
                        drawRoundRect(
                            hue,
                            topLeft = Offset(x0, 0f),
                            size = Size(x1 - x0, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f),
                        )
                    }
                }
                if (exercise.instructions.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("How to do it", style = ProType.label, color = c.text)
                    exercise.instructions.forEachIndexed { i, step ->
                        Text("${i + 1}. ${step.replace(Regex("<[^>]*>"), "")}", style = ProType.small, color = c.text2, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                P4Button("1RM Lab", onClick = { onLab() }, style = BtnStyle.SECONDARY, minHeight = 40)
                P4Button("Add to Workout", onClick = onAdd, style = BtnStyle.PRIMARY, minHeight = 40)
            }
        },
        dismissButton = { P4Button("Close", onClick = onClose, style = BtnStyle.GHOST, minHeight = 40) },
    )
}
