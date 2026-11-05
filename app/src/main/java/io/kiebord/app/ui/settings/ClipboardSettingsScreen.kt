package io.kiebord.app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kiebord.feature.ime.clipboard.ClipboardEntry
import io.kiebord.feature.ime.clipboard.SandboxedClipboardManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ClipPalette(
    val screenBg: Color,
    val cardBg: Color,
    val sheetBg: Color,
    val fieldBg: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val danger: Color
)

@Composable
private fun clipPalette(): ClipPalette =
    if (isSystemInDarkTheme()) {
        ClipPalette(
            screenBg = Color(0xFF121314),
            cardBg = Color(0xFF1E2022),
            sheetBg = Color(0xFF1E2022),
            fieldBg = Color(0xFF26282B),
            border = Color(0xFF3A3D40),
            textPrimary = Color(0xFFEDEDED),
            textSecondary = Color(0xFF9A9DA1),
            accent = Color(0xFF4CAF50),
            danger = Color(0xFFEF5350)
        )
    } else {
        ClipPalette(
            screenBg = Color(0xFFF0F1F3),
            cardBg = Color.White,
            sheetBg = Color.White,
            fieldBg = Color(0xFFF9F9F9),
            border = Color(0xFFDADCE0),
            textPrimary = Color(0xFF1C1C1E),
            textSecondary = Color(0xFF9A9A9F),
            accent = Color(0xFF43A047),
            danger = Color(0xFFC62828)
        )
    }

private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardSettingsScreen(
    clipboardManager: SandboxedClipboardManager,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val p = clipPalette()

    LaunchedEffect(Unit) {
        clipboardManager.syncFromSystemClipboard(context)
    }

    var showNewSheet by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }

    val allClips = clipboardManager.clips
    val pinnedClips = allClips.filter { it.isPinned }
    val recentClips = allClips.filter { !it.isPinned }
    val editingEntry = editingId?.let { id -> allClips.firstOrNull { it.id == id } }

    val listState = rememberLazyListState()
    var draggingId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(p.screenBg)
    ) {
        // ---------- Top bar ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(p.cardBg)
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = p.textPrimary
                )
            }
            Text(
                text = "Clipboard",
                color = p.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            IconButton(onClick = { showNewSheet = true }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "New clip",
                    tint = p.textPrimary
                )
            }
        }

        // ---------- Content ----------
        if (allClips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No copied clips yet",
                    color = p.textSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (pinnedClips.isNotEmpty()) {
                    item(key = "header_pinned") { SectionLabel("PINNED", p) }
                    items(pinnedClips, key = { it.id }) { entry ->
                        ClipCard(
                            entry = entry,
                            p = p,
                            isDragging = draggingId == entry.id,
                            dragOffset = if (draggingId == entry.id) dragOffset else 0f,
                            onClick = { editingId = entry.id },
                            onDragStart = { draggingId = entry.id; dragOffset = 0f },
                            onDrag = { dy ->
                                dragOffset += dy
                                handleDrag(
                                    entry, listState, clipboardManager,
                                    dragOffset
                                ) { dragOffset += it }
                            },
                            onDragEnd = { draggingId = null; dragOffset = 0f }
                        )
                    }
                }
                if (recentClips.isNotEmpty()) {
                    item(key = "header_recent") {
                        Spacer(Modifier.height(if (pinnedClips.isNotEmpty()) 6.dp else 0.dp))
                        SectionLabel("RECENT", p)
                    }
                    items(recentClips, key = { it.id }) { entry ->
                        ClipCard(
                            entry = entry,
                            p = p,
                            isDragging = draggingId == entry.id,
                            dragOffset = if (draggingId == entry.id) dragOffset else 0f,
                            onClick = { editingId = entry.id },
                            onDragStart = { draggingId = entry.id; dragOffset = 0f },
                            onDrag = { dy ->
                                dragOffset += dy
                                handleDrag(
                                    entry, listState, clipboardManager,
                                    dragOffset
                                ) { dragOffset += it }
                            },
                            onDragEnd = { draggingId = null; dragOffset = 0f }
                        )
                    }
                }
            }
        }
    }

    // ---------- New clip sheet ----------
    if (showNewSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var text by remember { mutableStateOf("") }

        val imagePicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                clipboardManager.addImageClip(context, uri)
                showNewSheet = false
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showNewSheet = false },
            sheetState = sheetState,
            containerColor = p.sheetBg,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Clip",
                        color = p.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SquareIconButton(
                        icon = Icons.Outlined.Image,
                        tint = p.accent,
                        borderColor = p.border,
                        onClick = { imagePicker.launch("image/*") }
                    )
                }
                Spacer(Modifier.height(18.dp))
                ClipTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Type your clip text here...",
                    p = p
                )
                Spacer(Modifier.height(20.dp))
                SheetButtons(
                    p = p,
                    saveEnabled = text.isNotBlank(),
                    onCancel = { showNewSheet = false },
                    onSave = {
                        clipboardManager.addClip(text.trim())
                        showNewSheet = false
                    }
                )
            }
        }
    }

    // ---------- Edit clip sheet ----------
    if (editingEntry != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var text by remember(editingEntry.id) { mutableStateOf(editingEntry.text) }

        ModalBottomSheet(
            onDismissRequest = { editingId = null },
            sheetState = sheetState,
            containerColor = p.sheetBg,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Clip",
                        color = p.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SquareIconButton(
                        icon = if (editingEntry.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        tint = if (editingEntry.isPinned) p.accent else p.textSecondary,
                        borderColor = p.border,
                        onClick = { clipboardManager.togglePin(editingEntry) }
                    )
                    SquareIconButton(
                        icon = Icons.Outlined.DeleteForever,
                        tint = p.danger,
                        borderColor = p.danger.copy(alpha = 0.35f),
                        onClick = {
                            clipboardManager.deleteClip(editingEntry)
                            editingId = null
                        }
                    )
                }
                Spacer(Modifier.height(18.dp))
                ClipTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Clip text",
                    p = p
                )
                Spacer(Modifier.height(20.dp))
                SheetButtons(
                    p = p,
                    saveEnabled = text.isNotBlank() && text != editingEntry.text,
                    onCancel = { editingId = null },
                    onSave = {
                        clipboardManager.updateClip(editingEntry, text.trim())
                        editingId = null
                    }
                )
            }
        }
    }
}

/** Drag-reorder helper: swaps with the item under the dragged card (same section only). */
private fun handleDrag(
    entry: ClipboardEntry,
    listState: androidx.compose.foundation.lazy.LazyListState,
    manager: SandboxedClipboardManager,
    dragOffset: Float,
    adjustOffset: (Float) -> Unit
) {
    val visible = listState.layoutInfo.visibleItemsInfo
    val me = visible.firstOrNull { it.key == entry.id } ?: return
    val center = me.offset + me.size / 2f + dragOffset
    val target = visible.firstOrNull { item ->
        val key = item.key as? String
        key != null && key != entry.id &&
            manager.clips.firstOrNull { it.id == key }?.isPinned == entry.isPinned &&
            center >= item.offset && center <= item.offset + item.size
    } ?: return
    manager.moveClip(entry.id, target.key as String)
    adjustOffset((me.offset - target.offset).toFloat())
}

@Composable
private fun SectionLabel(text: String, p: ClipPalette) {
    Text(
        text = text,
        color = p.textSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun ClipCard(
    entry: ClipboardEntry,
    p: ClipPalette,
    isDragging: Boolean,
    dragOffset: Float,
    onClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = dragOffset }
            .shadow(if (isDragging) 12.dp else 2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(p.cardBg)
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 10.dp, top = 16.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (entry.isOtp) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(p.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("OTP", color = p.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = entry.text,
                color = p.textPrimary,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = dateFormat.format(Date(entry.timestamp)),
                color = p.textSecondary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif
            )
        }

        if (entry.isPinned) {
            Icon(
                imageVector = Icons.Filled.PushPin,
                contentDescription = "Pinned",
                tint = p.accent,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(20.dp)
            )
        }

        Icon(
            imageVector = Icons.Outlined.DragHandle,
            contentDescription = "Reorder",
            tint = p.textSecondary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
                .pointerInput(entry.id) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() },
                        onDrag = { change, amount ->
                            change.consume()
                            onDrag(amount.y)
                        }
                    )
                }
        )
    }
}

@Composable
private fun SquareIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ClipTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    p: ClipPalette
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = p.textSecondary.copy(alpha = 0.7f), fontSize = 17.sp) },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 17.sp, lineHeight = 26.sp),
        minLines = 5,
        maxLines = 8,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = p.accent,
            unfocusedBorderColor = p.border,
            focusedTextColor = p.textPrimary,
            unfocusedTextColor = p.textPrimary,
            focusedContainerColor = p.fieldBg,
            unfocusedContainerColor = p.fieldBg,
            cursorColor = p.accent
        )
    )
}

@Composable
private fun SheetButtons(
    p: ClipPalette,
    saveEnabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, p.border.copy(alpha = 0.5f))
        ) {
            Text("Cancel", color = p.textSecondary, fontSize = 17.sp)
        }
        Button(
            onClick = onSave,
            enabled = saveEnabled,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = p.accent,
                contentColor = Color.White,
                disabledContainerColor = p.border.copy(alpha = 0.6f),
                disabledContentColor = p.textSecondary
            )
        ) {
            Text("Save", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}