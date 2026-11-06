package io.lipika.app.ui.settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.text.font.FontFamily
import io.lipika.theme.resolveKeyboardFontFamily
import io.lipika.theme.KeyboardFontFamily
import java.io.File
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.common.model.ShiftState
import io.lipika.data.layout.PredefinedLayouts
import io.lipika.data.prefs.LipikaPreferences
import io.lipika.feature.ime.ui.KeyboardRootView
import io.lipika.feature.ime.ui.ScaledKeyboardPreview
import io.lipika.theme.LipikaTheme
import io.lipika.theme.ThemeCategory
import io.lipika.theme.ThemeColorUtils
import io.lipika.theme.ThemeOptionItem
import io.lipika.theme.ThemeRepository

private data class PendingImportData(
    val option: ThemeOptionItem,
    val rawJson: String? = null,
    val stagingDir: java.io.File? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceThemesScreen(
    initialTab: Int = 0,
    preferences: LipikaPreferences,
    onUpdateTheme: (String) -> Unit,
    onUpdateHeightScale: (Float) -> Unit = {},
    onUpdateShowKeyBorders: (Boolean) -> Unit,
    onUpdateShowKeyShadows: (Boolean) -> Unit = {},
    pendingImportUri: Uri? = null,
    onClearPendingImportUri: () -> Unit = {},
    onOpenThemeDesigner: (editingThemeId: String?) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val themeRepo = remember(context) { ThemeRepository.getInstance(context) }
    var reloadTrigger by remember { mutableStateOf(0) }
    val allThemes = remember(reloadTrigger) { themeRepo.getAvailableThemes() }

    val scrollState = rememberScrollState()
    var previewThemeOption by remember { mutableStateOf<ThemeOptionItem?>(null) }
    var pendingImportTheme by remember { mutableStateOf<PendingImportData?>(null) }
    var showCreatePopup by remember { mutableStateOf(false) }
    val colors = LocalSettingsColors.current
    val scope = rememberCoroutineScope()

    fun processThemeUri(uri: Uri, onCompleted: (() -> Unit)? = null) {
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.readBytes()
                    }
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    val parseResult = withContext(Dispatchers.IO) {
                        themeRepo.parseThemeBytes(bytes)
                    }
                    parseResult.onSuccess { bundle ->
                        if (bundle.definitions.isNotEmpty()) {
                            val firstDef = bundle.definitions.first()
                            val richSpec = themeRepo.getRichTheme(firstDef.id)
                            val scheme = themeRepo.getColorScheme(firstDef.id)
                            val previewItem = ThemeOptionItem(
                                id = firstDef.id,
                                name = firstDef.name,
                                description = "Custom Lipika theme (${if (richSpec?.schemaVersion == 2) "v2" else "v1"})",
                                scheme = scheme,
                                isCustom = true,
                                category = ThemeCategory.CUSTOM
                            )
                            previewThemeOption = null
                            val rawJson = if (bundle.stagingDir == null) bytes.toString(Charsets.UTF_8) else null
                            pendingImportTheme = PendingImportData(
                                option = previewItem,
                                rawJson = rawJson,
                                stagingDir = bundle.stagingDir
                            )
                        } else {
                            Toast.makeText(context, "No themes found in file", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { err ->
                        Toast.makeText(context, "Invalid theme file: ${err.message ?: "Parse error"}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                onCompleted?.invoke()
            }
        }
    }

    LaunchedEffect(pendingImportUri) {
        val uri = pendingImportUri ?: return@LaunchedEffect
        processThemeUri(uri) {
            onClearPendingImportUri()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            processThemeUri(uri)
        }
    }

    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFD6F5F2),
                Color(0xFFF1FAF9),
                Color(0xFFE4F8F6),
                Color(0xFFD0F4F0)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 96.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0E332F),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable {
                            if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                                showCreatePopup = true
                            } else {
                                importLauncher.launch(arrayOf("*/*"))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Theme",
                        tint = Color(0xFF0E332F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Reference Header with Teal Palette Squircle Badge
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = Color(0x330E4D45),
                            spotColor = Color(0x330E4D45)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF2C857B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Customize your space",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0E332F),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Choose a theme that feels right for you",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5A7A77),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured 4 Themes in 2 Columns matching reference
            val featuredIds = listOf("ocean_teal", "sunset_coral", "minimal_grey", "soft_lavender")
            val featuredThemes = featuredIds.mapNotNull { id -> allThemes.find { it.id == id } }
            val otherThemes = allThemes.filter { it.id !in featuredIds && it.category != ThemeCategory.CUSTOM }
            val customThemes = allThemes.filter { it.category == ThemeCategory.CUSTOM }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                featuredThemes.chunked(2).forEach { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowThemes.forEach { themeOption ->
                            val isSelected = preferences.theme == themeOption.id
                            AbstractThemeCard(
                                themeOption = themeOption,
                                isSelected = isSelected,
                                showKeyBorders = preferences.showKeyBorders,
                                showKeyShadows = preferences.showKeyShadows,
                                modifier = Modifier.weight(1f),
                                onSelect = {
                                    onUpdateTheme(themeOption.id)
                                },
                                onLongClick = {
                                    previewThemeOption = themeOption
                                }
                            )
                        }
                        repeat(2 - rowThemes.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // More Themes Section (2 per row)
            if (otherThemes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "More Themes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0E332F),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    otherThemes.chunked(2).forEach { rowThemes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowThemes.forEach { themeOption ->
                                val isSelected = preferences.theme == themeOption.id
                                AbstractThemeCard(
                                    themeOption = themeOption,
                                    isSelected = isSelected,
                                    showKeyBorders = preferences.showKeyBorders,
                                    showKeyShadows = preferences.showKeyShadows,
                                    modifier = Modifier.weight(1f),
                                    onSelect = {
                                        onUpdateTheme(themeOption.id)
                                    },
                                    onLongClick = {
                                        previewThemeOption = themeOption
                                    }
                                )
                            }
                            repeat(2 - rowThemes.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Custom Themes Section (2 per row)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Custom Themes",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E332F),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val totalCustom = 1 + customThemes.size
                val numCustomRows = (totalCustom + 1) / 2
                for (rowIndex in 0 until numCustomRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        for (colIndex in 0 until 2) {
                            val itemIndex = rowIndex * 2 + colIndex
                            if (itemIndex == 0) {
                                BlankThemePlusCard(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                                            showCreatePopup = true
                                        } else {
                                            importLauncher.launch(arrayOf("*/*"))
                                        }
                                    }
                                )
                            } else if (itemIndex - 1 < customThemes.size) {
                                val themeOption = customThemes[itemIndex - 1]
                                val isSelected = preferences.theme == themeOption.id
                                AbstractThemeCard(
                                    themeOption = themeOption,
                                    isSelected = isSelected,
                                    showKeyBorders = preferences.showKeyBorders,
                                    showKeyShadows = preferences.showKeyShadows,
                                    modifier = Modifier.weight(1f),
                                    onSelect = {
                                        onUpdateTheme(themeOption.id)
                                    },
                                    onLongClick = {
                                        previewThemeOption = themeOption
                                    }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Docked "Continue" Pill Button (exact match to reference!)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00E4F8F6),
                            Color(0xCCE4F8F6),
                            Color(0xFFD6F5F2)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0E4D45)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }

    previewThemeOption?.let { currentPreview ->
        var previewShowKeyBorders by remember(currentPreview.id, preferences.showKeyBorders) {
            mutableStateOf(preferences.showKeyBorders)
        }
        var previewShowKeyShadows by remember(currentPreview.id, preferences.showKeyShadows) {
            mutableStateOf(preferences.showKeyShadows)
        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { previewThemeOption = null },
            sheetState = sheetState,
            containerColor = colors.cardBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ThemePreviewBottomSheetContent(
                themeOption = currentPreview,
                preferences = preferences,
                showKeyBorders = previewShowKeyBorders,
                showKeyShadows = previewShowKeyShadows,
                onToggleKeyBorders = { previewShowKeyBorders = it },
                onToggleKeyShadows = { previewShowKeyShadows = it },
                onApplyTheme = {
                    onUpdateTheme(currentPreview.id)
                    onUpdateShowKeyBorders(previewShowKeyBorders)
                    onUpdateShowKeyShadows(previewShowKeyShadows)
                    previewThemeOption = null
                },
                onEditTheme = if (currentPreview.isCustom && io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                    {
                        val targetId = currentPreview.id
                        previewThemeOption = null
                        onOpenThemeDesigner(targetId)
                    }
                } else null,
                onShareTheme = if (currentPreview.isCustom) {
                    {
                        val targetId = currentPreview.id
                        val targetName = currentPreview.name
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                themeRepo.exportThemeFileForSharing(targetId)
                            }
                            result.onSuccess { exportedFile ->
                                try {
                                    val shareUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        exportedFile
                                    )
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/octet-stream"
                                        putExtra(Intent.EXTRA_STREAM, shareUri)
                                        putExtra(Intent.EXTRA_SUBJECT, "$targetName.bornotheme")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(sendIntent, "Share $targetName")
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not share theme: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }.onFailure { err ->
                                Toast.makeText(context, "Export failed: ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else null,
                onDeleteTheme = if (currentPreview.isCustom) {
                    {
                        val targetId = currentPreview.id
                        val targetName = currentPreview.name
                        themeRepo.deleteCustomTheme(targetId)
                        if (preferences.theme == targetId) {
                            onUpdateTheme(LipikaPreferences.THEME_DEFAULT_DARK)
                        }
                        reloadTrigger++
                        previewThemeOption = null
                        Toast.makeText(context, "Deleted $targetName", Toast.LENGTH_SHORT).show()
                    }
                } else null
            )
        }
    }

    pendingImportTheme?.let { currentPending ->
        var importShowKeyBorders by remember(currentPending.option.id, preferences.showKeyBorders) {
            mutableStateOf(preferences.showKeyBorders)
        }
        var importShowKeyShadows by remember(currentPending.option.id, preferences.showKeyShadows) {
            mutableStateOf(preferences.showKeyShadows)
        }
        val importSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = {
                currentPending.stagingDir?.let { try { it.deleteRecursively() } catch (_: Exception) {} }
                pendingImportTheme = null
            },
            sheetState = importSheetState,
            containerColor = colors.cardBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ThemePreviewBottomSheetContent(
                themeOption = currentPending.option,
                preferences = preferences,
                showKeyBorders = importShowKeyBorders,
                showKeyShadows = importShowKeyShadows,
                onToggleKeyBorders = { importShowKeyBorders = it },
                onToggleKeyShadows = { importShowKeyShadows = it },
                onApplyTheme = {},
                isImportMode = true,
                onCancelImport = {
                    currentPending.stagingDir?.let { try { it.deleteRecursively() } catch (_: Exception) {} }
                    pendingImportTheme = null
                },
                onConfirmImport = {
                    if (currentPending.stagingDir != null) {
                        val res = themeRepo.importStagedTheme(currentPending.option.id, currentPending.stagingDir)
                        res.onSuccess { importedItem ->
                            reloadTrigger++
                            onUpdateTheme(importedItem.id)
                            onUpdateShowKeyBorders(importShowKeyBorders)
                            onUpdateShowKeyShadows(importShowKeyShadows)
                            Toast.makeText(context, "Imported ${importedItem.name}", Toast.LENGTH_SHORT).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Import failed: ${err.message ?: "Invalid theme package"}", Toast.LENGTH_LONG).show()
                        }
                    } else if (!currentPending.rawJson.isNullOrBlank()) {
                        val res = themeRepo.importTheme(currentPending.rawJson)
                        res.onSuccess { importedList ->
                            reloadTrigger++
                            if (importedList.isNotEmpty()) {
                                val first = importedList.first()
                                onUpdateTheme(first.id)
                                onUpdateShowKeyBorders(importShowKeyBorders)
                                onUpdateShowKeyShadows(importShowKeyShadows)
                                Toast.makeText(context, "Imported ${first.name}", Toast.LENGTH_SHORT).show()
                            }
                        }.onFailure { err ->
                            Toast.makeText(context, "Import failed: ${err.message ?: "Invalid theme file"}", Toast.LENGTH_LONG).show()
                        }
                    }
                    pendingImportTheme = null
                }
            )
        }
    }

    if (showCreatePopup) {
        AlertDialog(
            onDismissRequest = { showCreatePopup = false },
            containerColor = colors.cardBackground,
            title = {
                Text(
                    text = "New Theme",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Choose an option to add a keyboard theme:",
                        fontSize = 14.sp,
                        color = colors.textSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Button 1: Import (.bornotheme) - Squared Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (colors.isDark) Color(0xFF22242A) else Color(0xFFF1F3F9))
                                .border(1.5.dp, colors.accent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .clickable {
                                    showCreatePopup = false
                                    importLauncher.launch(arrayOf("*/*"))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(colors.accent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FileDownload,
                                        contentDescription = "Import",
                                        tint = colors.accent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Import",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = ".bornotheme",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Button 2: Create Theme - Squared Button
                        if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (colors.isDark) Color(0xFF22242A) else Color(0xFFF1F3F9))
                                    .border(1.5.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        showCreatePopup = false
                                        onOpenThemeDesigner(null)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(if (colors.isDark) Color(0xFF2C2C32) else Color(0xFFE2E2EC)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Palette,
                                            contentDescription = "Create",
                                            tint = colors.textPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Create",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Theme Studio",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCreatePopup = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun BlankThemePlusCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE2EFEF)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0FAF9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Create Theme",
                    tint = Color(0xFF2C857B),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Create Theme",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2A28),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Design or import",
                fontSize = 12.sp,
                color = Color(0xFF70807E),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AbstractThemeCard(
    themeOption: ThemeOptionItem,
    isSelected: Boolean,
    showKeyBorders: Boolean = true,
    showKeyShadows: Boolean = true,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val scheme = themeOption.scheme
    val cardShape = RoundedCornerShape(22.dp)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) Color(0xFF147268) else Color(0xFFE2EFEF)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Miniature Keyboard Preview Box (104.dp height, 16.dp rounded shape)
            val previewShape = RoundedCornerShape(16.dp)
            val gradientBrush = remember(scheme) {
                scheme.themeSpec?.background?.gradient?.toBrush(androidx.compose.ui.geometry.Size(260f, 160f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .clip(previewShape)
                    .then(
                        if (gradientBrush != null) {
                            Modifier.background(gradientBrush)
                        } else {
                            Modifier.background(scheme.containerBackground)
                        }
                    )
            ) {
                val imageSpec = scheme.themeSpec?.background?.image
                if (imageSpec != null) {
                    io.lipika.theme.ThemeImageBackgroundView(
                        spec = imageSpec,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // White / translucent keycap bar at bottom representing miniature keyboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(44.dp)
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    val keyShape = RoundedCornerShape(5.dp)
                    val spaceHex = scheme.themeSpec?.spaceKey?.background ?: scheme.themeSpec?.characterKey?.background
                    val spaceColor = if (spaceHex != null) ThemeColorUtils.parseHexColor(spaceHex, Color(0xFFF1F5F9)) else Color(0xFFF1F5F9)
                    val enterHex = scheme.themeSpec?.actionKey?.background
                    val enterColor = if (enterHex != null) ThemeColorUtils.parseHexColor(enterHex, scheme.accentColor) else scheme.accentColor

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Function / Sym key
                        Box(
                            modifier = Modifier
                                .weight(0.2f)
                                .height(22.dp)
                                .clip(keyShape)
                                .background(Color(0xFFE2E8F0))
                        )
                        // Space bar
                        Box(
                            modifier = Modifier
                                .weight(0.55f)
                                .height(22.dp)
                                .clip(keyShape)
                                .background(spaceColor)
                                .border(0.5.dp, Color(0x33000000), keyShape)
                        )
                        // Action / Enter key
                        Box(
                            modifier = Modifier
                                .weight(0.25f)
                                .height(22.dp)
                                .clip(keyShape)
                                .background(enterColor)
                        )
                    }
                }

                // Selected Checkmark Badge
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF147268))
                            .border(1.5.dp, Color.White, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Theme Name (Bold, 15.5.sp, dark color, centered)
            Text(
                text = themeOption.name,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2A28),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Theme Vibe / Description (Soft grey, 12.sp, centered)
            val vibeDescription = remember(themeOption.id, themeOption.description) {
                when (themeOption.id) {
                    "ocean_teal" -> "Fresh & energizing"
                    "sunset_coral" -> "Warm & creative"
                    "minimal_grey" -> "Clean & focused"
                    "soft_lavender" -> "Calm & soothing"
                    "frosted_glass" -> "Translucent & modern"
                    "lipika_gradient" -> "Luminous aurora glass"
                    "default_light" -> "Crisp & classic"
                    "default_dark" -> "Sleek velvet graphite"
                    "pure_black" -> "Pitch-black OLED"
                    "amoled_black" -> "True deep black"
                    else -> themeOption.description.take(24)
                }
            }

            Text(
                text = vibeDescription,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF70807E),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ThemePreviewBottomSheetContent(
    themeOption: ThemeOptionItem,
    preferences: LipikaPreferences,
    showKeyBorders: Boolean,
    showKeyShadows: Boolean,
    onToggleKeyBorders: (Boolean) -> Unit,
    onToggleKeyShadows: (Boolean) -> Unit,
    onApplyTheme: () -> Unit,
    onEditTheme: (() -> Unit)? = null,
    onShareTheme: (() -> Unit)? = null,
    onDeleteTheme: (() -> Unit)? = null,
    isImportMode: Boolean = false,
    onCancelImport: (() -> Unit)? = null,
    onConfirmImport: (() -> Unit)? = null
) {
    val colors = LocalSettingsColors.current

    val themeHasShadow = remember(themeOption.scheme) {
        val s = themeOption.scheme
        s.keyShadow.alpha > 0.01f ||
            (s.themeSpec?.characterKey?.shadow != null &&
             (s.themeSpec?.characterKey?.shadow?.elevationDp ?: 0f) > 0f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isImportMode) "Import: ${themeOption.name}" else themeOption.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        LipikaTheme(colorScheme = themeOption.scheme) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .background(themeOption.scheme.containerBackground)
            ) {
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val baseWidth = if (screenWidth > 500.dp) 390.dp else screenWidth
                val fullRowHeight = if (preferences.showNumberRow) 54.dp else 61.dp
                val targetRowHeight = fullRowHeight * preferences.keyboardHeightScale

                ScaledKeyboardPreview(
                    baseWidth = baseWidth,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KeyboardRootView(
                        layout = PredefinedLayouts.EnglishQwerty,
                        shiftState = ShiftState.UNSHIFTED,
                        onKeyTap = {},
                        onKeyLongPress = {},
                        showKeyBorders = showKeyBorders,
                        showKeyShadows = showKeyShadows,
                        defaultShowNumberRow = preferences.showNumberRow,
                        defaultShowSecondaryKeys = false,
                        initialRowHeight = targetRowHeight,
                        heightScalePortrait = preferences.keyboardHeightScale,
                        applySystemInsets = false,
                        isInteractive = false,
                        navBarBottomInset = 0.dp
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SettingsGroup(spacing = 2.dp) {
                SettingsToggleItem(
                    title = "Key Borders",
                    checked = showKeyBorders,
                    position = if (themeHasShadow) GroupPosition.TOP else GroupPosition.ALONE,
                    verticalPadding = 6.dp,
                    onCheckedChange = onToggleKeyBorders
                )
                if (themeHasShadow) {
                    SettingsToggleItem(
                        title = "Key Shadows",
                        checked = showKeyShadows,
                        position = GroupPosition.BOTTOM,
                        verticalPadding = 8.dp,
                        onCheckedChange = onToggleKeyShadows
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isImportMode) {
                Button(
                    onClick = { onConfirmImport?.invoke() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    )
                ) {
                    Text(
                        text = "Import Theme",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) Color(0xFF0F141C) else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onCancelImport?.invoke() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (colors.isDark) Color(0xFF2C2C32) else Color(0xFFE0E2EC),
                        contentColor = colors.textPrimary
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            } else {
                Button(
                    onClick = onApplyTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    )
                ) {
                    Text(
                        text = "Apply Theme",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) Color(0xFF0F141C) else Color.White
                    )
                }

                if (onEditTheme != null || onShareTheme != null || onDeleteTheme != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val neutralCircleBg = if (colors.isDark) Color(0xFF2C2C32) else Color(0xFFE0E2EC)

                        if (onEditTheme != null) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(neutralCircleBg)
                                    .border(1.dp, colors.cardBorder, CircleShape)
                                    .clickable(onClick = onEditTheme),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit Theme",
                                    tint = colors.textPrimary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }

                        if (onShareTheme != null) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(neutralCircleBg)
                                    .border(1.dp, colors.cardBorder, CircleShape)
                                    .clickable(onClick = onShareTheme),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share Theme",
                                    tint = colors.textPrimary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }

                        if (onDeleteTheme != null) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935))
                                    .clickable(onClick = onDeleteTheme),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Theme",
                                    tint = Color.White,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
    }
    return uri.lastPathSegment
}


