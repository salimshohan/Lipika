package io.lipika.app.ui.settings

import android.content.ClipData
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.feature.ime.clipboard.ClipboardEntry
import io.lipika.feature.ime.clipboard.SandboxedClipboardManager
import java.io.File
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
private fun clipPalette(): ClipPalette {
    val sysDark = isSystemInDarkTheme()
    val settingsColors = LocalSettingsColors.current
    return if (settingsColors.isDark || sysDark) {
        ClipPalette(
            screenBg = settingsColors.background,
            cardBg = settingsColors.cardBackground,
            sheetBg = settingsColors.cardBackground,
            fieldBg = settingsColors.inputBackground,
            border = settingsColors.cardBorder,
            textPrimary = settingsColors.textPrimary,
            textSecondary = settingsColors.textSecondary,
            accent = settingsColors.accent,
            danger = Color(0xFFEF5350)
        )
    } else {
        ClipPalette(
            screenBg = settingsColors.background,
            cardBg = settingsColors.cardBackground,
            sheetBg = Color.White,
            fieldBg = settingsColors.inputBackground,
            border = settingsColors.cardBorder,
            textPrimary = settingsColors.textPrimary,
            textSecondary = settingsColors.textSecondary,
            accent = settingsColors.accent,
            danger = Color(0xFFE53935)
        )
    }
}

private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardSettingsScreen(
    clipboardManager: SandboxedClipboardManager,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val strings = LocalLipikaStrings.current
    val isBn = strings === LipikaStringsBn
    val p = clipPalette()

    LaunchedEffect(Unit) {
        clipboardManager.syncFromSystemClipboard(context)
    }

    var searchQuery by remember { mutableStateOf("") }
    var showNewSheet by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }

    val allClips = clipboardManager.clips
    val filteredClips = if (searchQuery.isBlank()) {
        allClips
    } else {
        allClips.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    val pinnedClips = filteredClips.filter { it.isPinned }
    val recentClips = filteredClips.filter { !it.isPinned }
    val editingEntry = editingId?.let { id -> allClips.firstOrNull { it.id == id } }

    val listState = rememberLazyListState()
    var draggingId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            clipboardManager.addImageClip(context, uri)
            Toast.makeText(context, "Picture saved to clipboard", Toast.LENGTH_SHORT).show()
            showNewSheet = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(p.screenBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Compact Search & Action Bar (single header only, unified bar sits above this)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    placeholder = {
                        Text(
                            text = if (isBn) "ক্লিপ খুঁজুন..." else "Search clips...",
                            color = p.textSecondary.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = p.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = p.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
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

                // Quick image picker button
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(p.cardBg)
                        .border(BorderStroke(1.dp, p.border), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = "Add image",
                        tint = p.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete all unpinned clips
                if (recentClips.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            clipboardManager.clearAllUnpinned()
                            Toast.makeText(context, "Unpinned clips cleared", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(p.cardBg)
                            .border(BorderStroke(1.dp, p.border), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear unpinned",
                            tint = p.textSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Auto-Delete History Duration Bar
            var selectedExpiry by remember {
                mutableIntStateOf(
                    try {
                        io.lipika.data.prefs.LipikaPreferencesRepository.getInstance(context).getClipboardExpiryHoursSync()
                    } catch (_: Throwable) { 5 }
                )
            }
            var showExpiryDialog by remember { mutableStateOf(false) }

            val expiryLabel = when (selectedExpiry) {
                1 -> if (isBn) "১ ঘণ্টা" else "1 Hour"
                2 -> if (isBn) "২ ঘণ্টা" else "2 Hours"
                5 -> if (isBn) "৫ ঘণ্টা (ডিফল্ট)" else "5 Hours (Default)"
                6 -> if (isBn) "৬ ঘণ্টা" else "6 Hours"
                12 -> if (isBn) "১২ ঘণ্টা" else "12 Hours"
                24 -> if (isBn) "১ দিন" else "1 Day"
                360 -> if (isBn) "১৫ দিন" else "15 Days"
                720 -> if (isBn) "৩০ দিন" else "30 Days"
                -1 -> if (isBn) "কখনই না" else "Never"
                else -> if (isBn) "$selectedExpiry ঘণ্টা" else "$selectedExpiry Hours"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(p.cardBg)
                    .border(BorderStroke(1.dp, p.border), RoundedCornerShape(12.dp))
                    .clickable { showExpiryDialog = true }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = p.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBn) "অটো-ডিলিট সময়সীমা:" else "Auto-delete unpinned clips after:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = p.textSecondary
                    )
                }
                Text(
                    text = expiryLabel,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = p.accent
                )
            }

            if (showExpiryDialog) {
                val options = listOf(
                    1 to (if (isBn) "১ ঘণ্টা" else "1 Hour"),
                    2 to (if (isBn) "২ ঘণ্টা" else "2 Hours"),
                    5 to (if (isBn) "৫ ঘণ্টা (ডিফল্ট)" else "5 Hours (Default)"),
                    6 to (if (isBn) "৬ ঘণ্টা" else "6 Hours"),
                    12 to (if (isBn) "১২ ঘণ্টা" else "12 Hours"),
                    24 to (if (isBn) "১ দিন" else "1 Day"),
                    360 to (if (isBn) "১৫ দিন" else "15 Days"),
                    720 to (if (isBn) "৩০ দিন" else "30 Days"),
                    -1 to (if (isBn) "কখনই না" else "Never")
                )
                AlertDialog(
                    onDismissRequest = { showExpiryDialog = false },
                    title = {
                        Text(
                            text = if (isBn) "অটো-ডিলিটের সময় নির্ধারণ" else "Auto-Delete Retention Time",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = p.textPrimary
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBn) "নির্ধারিত সময়ের পুরনো অপিনকৃত ক্লিপ স্বয়ংক্রিয়ভাবে মুছে যাবে। পিন করা ক্লিপ কখনই মুছবে না।" else "Unpinned items older than this time will be deleted automatically. Pinned items are never deleted.",
                                fontSize = 12.sp,
                                color = p.textSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            for ((hours, label) in options) {
                                val isSelected = selectedExpiry == hours
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) p.accent.copy(alpha = 0.12f) else Color.Transparent)
                                        .clickable {
                                            selectedExpiry = hours
                                            showExpiryDialog = false
                                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                                io.lipika.data.prefs.LipikaPreferencesRepository.getInstance(context).updateClipboardExpiryHours(hours)
                                                clipboardManager.purgeExpiredClips()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) p.accent else p.textPrimary
                                    )
                                    if (isSelected) {
                                        Text("✓", color = p.accent, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showExpiryDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = p.accent)
                        ) {
                            Text("Done", color = Color.White)
                        }
                    }
                )
            }

            // Clips List
            if (filteredClips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = p.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching clips" else "No copied clips yet",
                            color = p.textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 14.dp,
                        end = 14.dp,
                        top = 6.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pinnedClips.isNotEmpty()) {
                        item(key = "header_pinned") { SectionBadge("PINNED (${pinnedClips.size})", p) }
                        items(pinnedClips, key = { it.id }) { entry ->
                            ClipCard(
                                entry = entry,
                                p = p,
                                isDragging = draggingId == entry.id,
                                dragOffset = if (draggingId == entry.id) dragOffset else 0f,
                                onClick = { editingId = entry.id },
                                onCopy = {
                                    val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    cm?.setPrimaryClip(ClipData.newPlainText("Copied", entry.text))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onTogglePin = { clipboardManager.togglePin(entry) },
                                onDragStart = { draggingId = entry.id; dragOffset = 0f },
                                onDrag = { dy ->
                                    dragOffset += dy
                                    handleDrag(entry, listState, clipboardManager, dragOffset) { dragOffset += it }
                                },
                                onDragEnd = { draggingId = null; dragOffset = 0f }
                            )
                        }
                    }

                    if (recentClips.isNotEmpty()) {
                        item(key = "header_recent") {
                            Spacer(Modifier.height(if (pinnedClips.isNotEmpty()) 4.dp else 0.dp))
                            SectionBadge("RECENT (${recentClips.size})", p)
                        }
                        items(recentClips, key = { it.id }) { entry ->
                            ClipCard(
                                entry = entry,
                                p = p,
                                isDragging = draggingId == entry.id,
                                dragOffset = if (draggingId == entry.id) dragOffset else 0f,
                                onClick = { editingId = entry.id },
                                onCopy = {
                                    val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    cm?.setPrimaryClip(ClipData.newPlainText("Copied", entry.text))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onTogglePin = { clipboardManager.togglePin(entry) },
                                onDragStart = { draggingId = entry.id; dragOffset = 0f },
                                onDrag = { dy ->
                                    dragOffset += dy
                                    handleDrag(entry, listState, clipboardManager, dragOffset) { dragOffset += it }
                                },
                                onDragEnd = { draggingId = null; dragOffset = 0f }
                            )
                        }
                    }
                }
            }
        }

        // Modern Solid Floating Action Button
        FloatingActionButton(
            onClick = { showNewSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = p.accent,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New clip",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // ---------- New clip sheet ----------
    if (showNewSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var text by remember { mutableStateOf("") }

        ModalBottomSheet(
            onDismissRequest = { showNewSheet = false },
            sheetState = sheetState,
            containerColor = p.sheetBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Clip",
                        color = p.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SolidIconButton(
                        icon = Icons.Filled.Image,
                        tint = p.accent,
                        borderColor = p.border,
                        onClick = { imagePicker.launch("image/*") }
                    )
                }
                Spacer(Modifier.height(14.dp))
                ClipTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Type or paste clip text here...",
                    p = p
                )
                Spacer(Modifier.height(16.dp))
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
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (!editingEntry.imagePath.isNullOrBlank()) "Image Clip" else "Edit Clip",
                        color = p.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SolidIconButton(
                        icon = Icons.Filled.ContentCopy,
                        tint = p.textPrimary,
                        borderColor = p.border,
                        onClick = {
                            val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                            cm?.setPrimaryClip(ClipData.newPlainText("Copied", editingEntry.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SolidIconButton(
                        icon = Icons.Filled.PushPin,
                        tint = if (editingEntry.isPinned) p.accent else p.textSecondary,
                        borderColor = p.border,
                        onClick = { clipboardManager.togglePin(editingEntry) }
                    )
                    SolidIconButton(
                        icon = Icons.Filled.Delete,
                        tint = p.danger,
                        borderColor = p.danger.copy(alpha = 0.35f),
                        onClick = {
                            clipboardManager.deleteClip(editingEntry)
                            editingId = null
                        }
                    )
                }
                Spacer(Modifier.height(14.dp))

                // Show preview if image
                val imageBitmap = remember(editingEntry.imagePath, editingEntry.text) {
                    runCatching {
                        val path = editingEntry.imagePath
                        if (!path.isNullOrBlank() && File(path).exists()) {
                            BitmapFactory.decodeFile(path)?.asImageBitmap()
                        } else if (editingEntry.text.startsWith("content://")) {
                            context.contentResolver.openInputStream(Uri.parse(editingEntry.text))?.use {
                                BitmapFactory.decodeStream(it)?.asImageBitmap()
                            }
                        } else null
                    }.getOrNull()
                }

                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(Modifier.height(14.dp))
                }

                if (editingEntry.imagePath.isNullOrBlank() && !editingEntry.text.startsWith("content://")) {
                    ClipTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "Clip text",
                        p = p
                    )
                    Spacer(Modifier.height(16.dp))
                    SheetButtons(
                        p = p,
                        saveEnabled = text.isNotBlank() && text != editingEntry.text,
                        onCancel = { editingId = null },
                        onSave = {
                            clipboardManager.updateClip(editingEntry, text.trim())
                            editingId = null
                        }
                    )
                } else {
                    OutlinedButton(
                        onClick = { editingId = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Close", color = p.textPrimary, fontSize = 15.sp)
                    }
                }
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
private fun SectionBadge(text: String, p: ClipPalette) {
    Text(
        text = text,
        color = p.textSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
    )
}

@Composable
private fun ClipCard(
    entry: ClipboardEntry,
    p: ClipPalette,
    isDragging: Boolean,
    dragOffset: Float,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onTogglePin: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    val context = LocalContext.current

    // Resolve Image if entry has imagePath or content URI
    val imageBitmap = remember(entry.imagePath, entry.text) {
        runCatching {
            val path = entry.imagePath
            if (!path.isNullOrBlank() && File(path).exists()) {
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            } else if (entry.text.startsWith("content://")) {
                context.contentResolver.openInputStream(Uri.parse(entry.text))?.use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } else null
        }.getOrNull()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = dragOffset }
            .shadow(if (isDragging) 8.dp else 1.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(p.cardBg)
            .border(BorderStroke(1.dp, p.border.copy(alpha = 0.65f)), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (entry.isOtp) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(p.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("OTP", color = p.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Image Preview or Text
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Clipboard Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                if (entry.text.isNotBlank() && entry.text != "[Image]" && !entry.text.startsWith("content://")) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.text,
                        color = p.textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = entry.text,
                    color = p.textPrimary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(5.dp))
            Text(
                text = dateFormat.format(Date(entry.timestamp)),
                color = p.textSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.width(8.dp))

        // Quick Pin Toggle
        IconButton(
            onClick = onTogglePin,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PushPin,
                contentDescription = if (entry.isPinned) "Unpin" else "Pin",
                tint = if (entry.isPinned) p.accent else p.textSecondary.copy(alpha = 0.35f),
                modifier = Modifier.size(17.dp)
            )
        }

        // Quick Copy Button
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy",
                tint = p.textSecondary.copy(alpha = 0.55f),
                modifier = Modifier.size(16.dp)
            )
        }

        // Solid Reorder Drag Handle
        Icon(
            imageVector = Icons.Filled.DragHandle,
            contentDescription = "Reorder",
            tint = p.textSecondary.copy(alpha = 0.45f),
            modifier = Modifier
                .size(32.dp)
                .padding(6.dp)
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
private fun SolidIconButton(
    icon: ImageVector,
    tint: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
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
        placeholder = { Text(placeholder, color = p.textSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
        minLines = 4,
        maxLines = 7,
        shape = RoundedCornerShape(14.dp),
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
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, p.border)
        ) {
            Text("Cancel", color = p.textSecondary, fontSize = 14.sp)
        }
        Button(
            onClick = onSave,
            enabled = saveEnabled,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = p.accent,
                contentColor = Color.White,
                disabledContainerColor = p.border.copy(alpha = 0.5f),
                disabledContentColor = p.textSecondary
            )
        ) {
            Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
