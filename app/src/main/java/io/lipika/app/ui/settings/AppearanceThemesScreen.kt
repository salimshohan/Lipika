package io.lipika.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.core.content.FileProvider
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private data class PendingImportData(
    val option: ThemeOptionItem,
    val rawJson: String? = null,
    val stagingDir: File? = null
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
    var showMyThemesSheet by remember { mutableStateOf(false) }
    var themeToRename by remember { mutableStateOf<ThemeOptionItem?>(null) }
    var renameInputText by remember { mutableStateOf("") }
    var themeToDelete by remember { mutableStateOf<ThemeOptionItem?>(null) }

    val colors = LocalSettingsColors.current
    val scope = rememberCoroutineScope()

    val customThemes = remember(allThemes) {
        allThemes.filter { it.category == ThemeCategory.CUSTOM || it.isCustom }
    }

    // Default Themes (Default Light & Default Dark)
    val defaultThemes = remember(allThemes) {
        val defs = allThemes.filter { it.id == "default_light" || it.id == "default_dark" }
        // Ensure default_light is first, default_dark second
        defs.sortedBy { if (it.id == "default_light") 0 else 1 }
    }

    // Color Themes (solid colors: pure_black, amoled_black, Borno color themes)
    val colorThemes = remember(allThemes) {
        allThemes.filter {
            (it.id == "pure_black" || it.id == "amoled_black" || it.category == ThemeCategory.BORNO_COLORS) &&
                    it.category != ThemeCategory.CUSTOM && !it.isCustom
        }
    }

    // Gradient Themes (ocean_teal, sunset_coral, minimal_grey, soft_lavender, frosted_glass, lipika_gradient, tokyo_night, nord, Borno gradients)
    val gradientThemes = remember(allThemes) {
        val namedGradientIds = setOf(
            "ocean_teal", "sunset_coral", "minimal_grey", "soft_lavender",
            "frosted_glass", "lipika_gradient", "tokyo_night", "nord"
        )
        allThemes.filter {
            (it.id in namedGradientIds || it.category == ThemeCategory.BORNO_GRADIENT) &&
                    it.id != "default_light" && it.id != "default_dark" &&
                    it.id != "pure_black" && it.id != "amoled_black" &&
                    it.category != ThemeCategory.CUSTOM && !it.isCustom
        }
    }

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
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(bottom = 36.dp)
        ) {
            // ==========================================
            // TOP SECTION: Back Button & Header
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Theme Gallery",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E332F)
                    )
                    Text(
                        text = "Customize keyboard colors & styles",
                        fontSize = 12.5.sp,
                        color = Color(0xFF5A7A77)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // TOP SECTION: Create Theme & My Themes Options
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopActionOptionCard(
                    title = "Create Theme",
                    subtitle = "Design in Studio",
                    icon = Icons.Outlined.Palette,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                            onOpenThemeDesigner(null)
                        } else {
                            importLauncher.launch(arrayOf("*/*"))
                        }
                    }
                )

                TopActionOptionCard(
                    title = "My Themes",
                    subtitle = "${customThemes.size} saved",
                    icon = Icons.Outlined.Folder,
                    modifier = Modifier.weight(1f),
                    onClick = { showMyThemesSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ==========================================
            // THEME CATEGORY 1: DEFAULT THEMES
            // ==========================================
            if (defaultThemes.isNotEmpty()) {
                ThemeCategoryHeader(title = "Default Themes")
                Spacer(modifier = Modifier.height(10.dp))

                ThemeGridTwoPerRow(
                    themes = defaultThemes,
                    activeThemeId = preferences.theme,
                    showKeyBorders = preferences.showKeyBorders,
                    showKeyShadows = preferences.showKeyShadows,
                    onThemeClick = { themeOption -> previewThemeOption = themeOption }
                )

                Spacer(modifier = Modifier.height(26.dp))
            }

            // ==========================================
            // THEME CATEGORY 2: COLOR THEMES
            // ==========================================
            if (colorThemes.isNotEmpty()) {
                ThemeCategoryHeader(title = "Color Themes")
                Spacer(modifier = Modifier.height(10.dp))

                ThemeGridTwoPerRow(
                    themes = colorThemes,
                    activeThemeId = preferences.theme,
                    showKeyBorders = preferences.showKeyBorders,
                    showKeyShadows = preferences.showKeyShadows,
                    onThemeClick = { themeOption -> previewThemeOption = themeOption }
                )

                Spacer(modifier = Modifier.height(26.dp))
            }

            // ==========================================
            // THEME CATEGORY 3: GRADIENT THEMES
            // ==========================================
            if (gradientThemes.isNotEmpty()) {
                ThemeCategoryHeader(title = "Gradient Themes")
                Spacer(modifier = Modifier.height(10.dp))

                ThemeGridTwoPerRow(
                    themes = gradientThemes,
                    activeThemeId = preferences.theme,
                    showKeyBorders = preferences.showKeyBorders,
                    showKeyShadows = preferences.showKeyShadows,
                    onThemeClick = { themeOption -> previewThemeOption = themeOption }
                )
            }
        }
    }

    // ==========================================
    // THEME CUSTOMIZATION INSPECTOR BOTTOM SHEET
    // ==========================================
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
                    Toast.makeText(context, "Theme '${currentPreview.name}' applied", Toast.LENGTH_SHORT).show()
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
                        themeToDelete = currentPreview
                    }
                } else null
            )
        }
    }

    // ==========================================
    // MY THEMES BOTTOM SHEET
    // ==========================================
    if (showMyThemesSheet) {
        val myThemesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showMyThemesSheet = false },
            sheetState = myThemesSheetState,
            containerColor = colors.cardBackground,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Custom Themes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Button(
                        onClick = {
                            showMyThemesSheet = false
                            if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                                onOpenThemeDesigner(null)
                            } else {
                                importLauncher.launch(arrayOf("*/*"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "New",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "New Theme", fontSize = 13.sp, color = Color.White)
                    }
                }

                if (customThemes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = null,
                                tint = colors.textSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No custom themes yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Create unique themes with custom colors in Theme Studio.",
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    customThemes.forEachIndexed { index, customTheme ->
                        val sequentialName = remember(customTheme.id, customTheme.name) {
                            if (customTheme.name.isNotBlank() && !customTheme.name.startsWith("custom_") && customTheme.name != "Custom Theme") {
                                customTheme.name
                            } else {
                                "Lipika Theme ${index + 1}"
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                            border = BorderStroke(1.dp, colors.cardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Miniature preview box
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(customTheme.scheme.containerBackground)
                                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            showMyThemesSheet = false
                                            previewThemeOption = customTheme
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Palette,
                                        contentDescription = null,
                                        tint = customTheme.scheme.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sequentialName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = if (preferences.theme == customTheme.id) "Active Theme" else "Custom Theme",
                                        fontSize = 12.sp,
                                        color = if (preferences.theme == customTheme.id) colors.accent else colors.textSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Apply button
                                    Button(
                                        onClick = {
                                            onUpdateTheme(customTheme.id)
                                            Toast.makeText(context, "Theme '$sequentialName' applied", Toast.LENGTH_SHORT).show()
                                            showMyThemesSheet = false
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(text = "Apply", fontSize = 12.sp, color = Color.White)
                                    }

                                    // Rename button
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.iconBackground)
                                            .clickable {
                                                themeToRename = customTheme
                                                renameInputText = sequentialName
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DriveFileRenameOutline,
                                            contentDescription = "Rename",
                                            tint = colors.textPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Edit button
                                    if (io.lipika.theme.designer.ThemeDesignerRegistry.isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(colors.iconBackground)
                                                .clickable {
                                                    showMyThemesSheet = false
                                                    onOpenThemeDesigner(customTheme.id)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = "Edit",
                                                tint = colors.textPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Delete button
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.iconBackground)
                                            .clickable {
                                                themeToDelete = customTheme
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // ==========================================
    // RENAME DIALOG
    // ==========================================
    themeToRename?.let { targetTheme ->
        AlertDialog(
            onDismissRequest = { themeToRename = null },
            containerColor = colors.cardBackground,
            title = {
                Text(
                    text = "Rename Theme",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = renameInputText,
                    onValueChange = { renameInputText = it },
                    singleLine = true,
                    label = { Text("Theme Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newName = renameInputText.trim()
                        if (newName.isNotBlank()) {
                            themeRepo.renameCustomTheme(targetTheme.id, newName)
                            reloadTrigger++
                            Toast.makeText(context, "Renamed to '$newName'", Toast.LENGTH_SHORT).show()
                        }
                        themeToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { themeToRename = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }

    // ==========================================
    // DELETE CONFIRMATION DIALOG
    // ==========================================
    themeToDelete?.let { targetTheme ->
        AlertDialog(
            onDismissRequest = { themeToDelete = null },
            containerColor = colors.cardBackground,
            title = {
                Text(
                    text = "Delete Theme",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${targetTheme.name}'? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = targetTheme.name
                        themeRepo.deleteCustomTheme(targetTheme.id)
                        if (preferences.theme == targetTheme.id) {
                            onUpdateTheme(LipikaPreferences.THEME_DEFAULT_DARK)
                        }
                        reloadTrigger++
                        previewThemeOption = null
                        themeToDelete = null
                        Toast.makeText(context, "Deleted '$name'", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { themeToDelete = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }

    // Pending Import Sheet
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
                onConfirmImport = {
                    val raw = currentPending.rawJson
                    val stDir = currentPending.stagingDir
                    val importResult = when {
                        stDir != null -> themeRepo.importStagedTheme(currentPending.option.id, stDir)
                        raw != null -> themeRepo.importTheme(raw).map { it.first() }
                        else -> Result.failure(IllegalStateException("No theme data to import"))
                    }
                    importResult.onSuccess { importedItem ->
                        reloadTrigger++
                        onUpdateTheme(importedItem.id)
                        onUpdateShowKeyBorders(importShowKeyBorders)
                        onUpdateShowKeyShadows(importShowKeyShadows)
                        Toast.makeText(context, "Theme '${importedItem.name}' imported", Toast.LENGTH_SHORT).show()
                    }.onFailure { err ->
                        Toast.makeText(context, "Import failed: ${err.message}", Toast.LENGTH_LONG).show()
                    }
                    pendingImportTheme = null
                },
                onCancelImport = {
                    currentPending.stagingDir?.let { try { it.deleteRecursively() } catch (_: Exception) {} }
                    pendingImportTheme = null
                }
            )
        }
    }
}

@Composable
private fun ThemeCategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.5.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0E332F),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
    )
}

@Composable
private fun TopActionOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(18.dp)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE2EFEF)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F4F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF147268),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0E332F)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF5A7A77),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ThemeGridTwoPerRow(
    themes: List<ThemeOptionItem>,
    activeThemeId: String,
    showKeyBorders: Boolean,
    showKeyShadows: Boolean,
    onThemeClick: (ThemeOptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        themes.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowThemes.forEach { themeOption ->
                    val isSelected = activeThemeId == themeOption.id
                    AbstractThemeCard(
                        themeOption = themeOption,
                        isSelected = isSelected,
                        showKeyBorders = showKeyBorders,
                        showKeyShadows = showKeyShadows,
                        modifier = Modifier.weight(1f),
                        onSelect = { onThemeClick(themeOption) }
                    )
                }
                repeat(2 - rowThemes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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
    onSelect: () -> Unit
) {
    val scheme = themeOption.scheme
    val cardShape = RoundedCornerShape(22.dp)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
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

            Text(
                text = themeOption.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2A28),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

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
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
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
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
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
                                    modifier = Modifier.size(20.dp)
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
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (onDeleteTheme != null) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(neutralCircleBg)
                                    .border(1.dp, colors.cardBorder, CircleShape)
                                    .clickable(onClick = onDeleteTheme),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Theme",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
