package io.lipika.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SmartButton
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.data.prefs.LipikaPreferences

data class AccentPreset(
    val key: String,
    val name: String,
    val color: Color,
    val hexCode: String
)

val CuratedAccentPresets = listOf(
    AccentPreset(LipikaPreferences.ACCENT_PREMIUM_BLUE, "Premium Blue", Color(0xFF0284C7), "#0284C7"),
    AccentPreset(LipikaPreferences.ACCENT_ROYAL_BLUE, "Royal Blue", Color(0xFF2563EB), "#2563EB"),
    AccentPreset(LipikaPreferences.ACCENT_SKY_BLUE, "Sky Blue", Color(0xFF0EA5E9), "#0EA5E9"),
    AccentPreset(LipikaPreferences.ACCENT_TEAL, "Teal", Color(0xFF0D9488), "#0D9488"),
    AccentPreset(LipikaPreferences.ACCENT_MINT_GREEN, "Mint Green", Color(0xFF10B981), "#10B981"),
    AccentPreset(LipikaPreferences.ACCENT_EMERALD, "Emerald", Color(0xFF059669), "#059669"),
    AccentPreset(LipikaPreferences.ACCENT_PURPLE, "Purple", Color(0xFF7C3AED), "#7C3AED"),
    AccentPreset(LipikaPreferences.ACCENT_LAVENDER, "Lavender", Color(0xFF8B5CF6), "#8B5CF6"),
    AccentPreset(LipikaPreferences.ACCENT_VIOLET, "Violet", Color(0xFF6D28D9), "#6D28D9"),
    AccentPreset(LipikaPreferences.ACCENT_PINK, "Pink", Color(0xFFEC4899), "#EC4899"),
    AccentPreset(LipikaPreferences.ACCENT_ROSE, "Rose", Color(0xFFF43F5E), "#F43F5E"),
    AccentPreset(LipikaPreferences.ACCENT_CORAL, "Coral", Color(0xFFF97316), "#F97316"),
    AccentPreset(LipikaPreferences.ACCENT_ORANGE, "Orange", Color(0xFFEA580C), "#EA580C"),
    AccentPreset(LipikaPreferences.ACCENT_AMBER, "Amber", Color(0xFFD97706), "#D97706"),
    AccentPreset(LipikaPreferences.ACCENT_GOLD, "Gold", Color(0xFFCA8A04), "#CA8A04"),
    AccentPreset(LipikaPreferences.ACCENT_RED, "Red", Color(0xFFDC2626), "#DC2626"),
    AccentPreset(LipikaPreferences.ACCENT_NEUTRAL_SLATE, "Slate", Color(0xFF475569), "#475569"),
    AccentPreset(LipikaPreferences.ACCENT_CYAN, "Pastel Cyan", Color(0xFF06B6D4), "#06B6D4")
)

@Composable
fun AppearanceScreen(
    preferences: LipikaPreferences,
    onUpdateAccentMode: (String) -> Unit = {},
    onUpdateAccentColor: (String) -> Unit = {},
    onUpdateComponentAccent: (String, String) -> Unit = { _, _ -> },
    onResetAccents: () -> Unit = {},
    onUpdateDisplayMode: (String) -> Unit = {},
    onUpdateAppTheme: (String) -> Unit = {},
    onUpdateKeyboardThemeMode: (String) -> Unit = {},
    onUpdateDarkStyle: (String) -> Unit = {},
    onUpdateThemeSchedule: (String) -> Unit = {},
    onUpdateCustomScheduleHours: (Int, Int) -> Unit = { _, _ -> },
    onOpenThemeStudio: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = LocalSettingsColors.current
    val scrollState = rememberScrollState()

    var customHexInput by remember { mutableStateOf("") }
    var selectedComponentKey by remember { mutableStateOf(LipikaPreferences.ACCENT_COMPONENT_CARDS) }

    val isMultiMode = preferences.accentMode == LipikaPreferences.ACCENT_MODE_MULTIPLE
    val multiAccentsMap = remember(preferences.multipleAccentsRaw) {
        preferences.getMultipleAccentsMap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.cardBackground)
                    .border(1.dp, colors.cardBorder, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Appearance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Accent colors & automated schedule",
                    fontSize = 12.5.sp,
                    color = colors.textSecondary
                )
            }
        }

        // ==========================================
        // SECTION 1: ACCENT COLOR SYSTEM
        // ==========================================
        AppearanceSection(
            title = "Accent Customization",
            description = "Select whether to use a uniform global accent or customize each UI component independently.",
            icon = Icons.Outlined.ColorLens
        ) {
            // Mode Selector: Single vs Multiple
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccentModeCard(
                    title = "Single Color",
                    subtitle = "One accent across all controls",
                    icon = Icons.Outlined.Palette,
                    selected = !isMultiMode,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateAccentMode(LipikaPreferences.ACCENT_MODE_SINGLE) }
                )
                AccentModeCard(
                    title = "Multiple Colors",
                    subtitle = "Distinct colors for cards, nav & icons",
                    icon = Icons.Outlined.Layers,
                    selected = isMultiMode,
                    modifier = Modifier.weight(1f),
                    onClick = { onUpdateAccentMode(LipikaPreferences.ACCENT_MODE_MULTIPLE) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isMultiMode) {
                // ==========================================
                // SINGLE COLOR PALETTE & CUSTOM HEX
                // ==========================================
                Text(
                    text = "Curated Palette",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                AccentPaletteGrid(
                    selectedKey = preferences.accentColor,
                    onSelectKey = { key -> onUpdateAccentColor(key) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Hex Input
                CustomHexInputRow(
                    value = customHexInput,
                    onValueChange = { customHexInput = it },
                    onApplyHex = { hex ->
                        onUpdateAccentColor(hex)
                        customHexInput = ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Live Interactive Component Preview
                Text(
                    text = "Live Preview",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LiveSingleAccentPreviewCard(accentColor = colors.accent)

            } else {
                // ==========================================
                // MULTIPLE ACCENTS: PER-COMPONENT CONFIG
                // ==========================================
                Text(
                    text = "Select Component to Customize",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val componentList = listOf(
                    LipikaPreferences.ACCENT_COMPONENT_CARDS to "Cards",
                    LipikaPreferences.ACCENT_COMPONENT_NAV to "Navigation",
                    LipikaPreferences.ACCENT_COMPONENT_ICONS to "Icons",
                    LipikaPreferences.ACCENT_COMPONENT_CONTROLS to "Controls",
                    LipikaPreferences.ACCENT_COMPONENT_BUTTONS to "Buttons"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    componentList.forEach { (key, label) ->
                        val isSelected = selectedComponentKey == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.accent.copy(alpha = 0.2f) else colors.cardBackground)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) colors.accent else colors.cardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedComponentKey = key }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) colors.accent else colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val activeComponentHex = multiAccentsMap[selectedComponentKey] ?: preferences.accentColor
                Text(
                    text = "Color for ${componentList.find { it.first == selectedComponentKey }?.second ?: "Component"}",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                AccentPaletteGrid(
                    selectedKey = activeComponentHex,
                    onSelectKey = { hexOrKey ->
                        val hex = CuratedAccentPresets.find { it.key == hexOrKey }?.hexCode ?: hexOrKey
                        onUpdateComponentAccent(selectedComponentKey, hex)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomHexInputRow(
                    value = customHexInput,
                    onValueChange = { customHexInput = it },
                    onApplyHex = { hex ->
                        onUpdateComponentAccent(selectedComponentKey, hex)
                        customHexInput = ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Live Multi-Component Preview
                Text(
                    text = "Multi-Accent Live Harmony",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LiveMultiAccentPreviewCard(
                    cardAccent = colors.cardAccent,
                    navAccent = colors.navAccent,
                    iconAccent = colors.iconAccent,
                    controlAccent = colors.controlAccent,
                    buttonAccent = colors.buttonAccent
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onResetAccents,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reset",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset All Accents to Default",
                        fontSize = 13.5.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ==========================================
        // SECTION 2: THEME SCHEDULE
        // ==========================================
        AppearanceSection(
            title = "Theme Schedule",
            description = "Automatically switch between light and dark display modes according to time or natural day cycle.",
            icon = Icons.Outlined.Schedule
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppearanceOptionCard(
                    title = "Always Light",
                    subtitle = "Always maintain the crisp light theme",
                    icon = Icons.Outlined.LightMode,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_LIGHT,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_LIGHT) }
                )
                AppearanceOptionCard(
                    title = "Always Dark",
                    subtitle = "Always maintain the dark theme",
                    icon = Icons.Outlined.DarkMode,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_DARK,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_DARK) }
                )
                AppearanceOptionCard(
                    title = "Sunset to Sunrise",
                    subtitle = "Automatically toggle dark mode at dusk (6 PM to 6 AM)",
                    icon = Icons.Outlined.WbTwilight,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE) }
                )
                AppearanceOptionCard(
                    title = "Custom Schedule",
                    subtitle = "Define specific hours for your dark mode preference",
                    icon = Icons.Outlined.Tune,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_CUSTOM) }
                )
            }

            AnimatedVisibility(
                visible = preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                var startHour by remember(preferences.customScheduleStartHour) {
                    mutableFloatStateOf(preferences.customScheduleStartHour.toFloat())
                }
                var endHour by remember(preferences.customScheduleEndHour) {
                    mutableFloatStateOf(preferences.customScheduleEndHour.toFloat())
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Custom Schedule Hours",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Dark Mode Starts", fontSize = 13.sp, color = colors.textSecondary)
                                Text(
                                    text = formatHour(startHour.toInt()),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                            Slider(
                                value = startHour,
                                onValueChange = { startHour = it },
                                onValueChangeFinished = {
                                    onUpdateCustomScheduleHours(startHour.toInt(), endHour.toInt())
                                },
                                valueRange = 0f..23f,
                                steps = 22,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.controlAccent,
                                    activeTrackColor = colors.controlAccent
                                )
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Dark Mode Ends", fontSize = 13.sp, color = colors.textSecondary)
                                Text(
                                    text = formatHour(endHour.toInt()),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                            Slider(
                                value = endHour,
                                onValueChange = { endHour = it },
                                onValueChangeFinished = {
                                    onUpdateCustomScheduleHours(startHour.toInt(), endHour.toInt())
                                },
                                valueRange = 0f..23f,
                                steps = 22,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.controlAccent,
                                    activeTrackColor = colors.controlAccent
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AppearanceSection(
    title: String,
    description: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    val colors = LocalSettingsColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }

        Text(
            text = description,
            fontSize = 13.sp,
            color = colors.textSecondary,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        content()
    }
}

@Composable
private fun AccentModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalSettingsColors.current
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .shadow(
                elevation = if (selected) 3.dp else 1.5.dp,
                shape = cardShape,
                ambientColor = Color(0x14000000),
                spotColor = Color(0x20000000)
            )
            .clip(cardShape)
            .clickable(onClick = onClick),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colors.accent.copy(alpha = 0.12f) else colors.cardBackground
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) colors.accent else colors.cardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) colors.accent.copy(alpha = 0.2f) else colors.iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (selected) colors.accent else colors.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (selected) {
                                Modifier.background(colors.accent)
                            } else {
                                Modifier.border(1.dp, colors.cardBorder, RoundedCornerShape(6.dp))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun AccentPaletteGrid(
    selectedKey: String,
    onSelectKey: (String) -> Unit
) {
    val colors = LocalSettingsColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CuratedAccentPresets.chunked(6).forEach { rowPresets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowPresets.forEach { preset ->
                    val isSelected = selectedKey.equals(preset.key, ignoreCase = true) ||
                            selectedKey.equals(preset.hexCode, ignoreCase = true)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSelectKey(preset.key) }
                            .padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(
                                    elevation = if (isSelected) 4.dp else 1.dp,
                                    shape = CircleShape,
                                    ambientColor = preset.color.copy(alpha = 0.4f),
                                    spotColor = preset.color.copy(alpha = 0.6f)
                                )
                                .clip(CircleShape)
                                .background(preset.color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = preset.name.split(" ").first(),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colors.accent else colors.textSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomHexInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onApplyHex: (String) -> Unit
) {
    val colors = LocalSettingsColors.current
    val parsedColor = remember(value) {
        val trimmed = value.trim()
        val hex = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
        if (hex.length == 7 || hex.length == 9) {
            runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
        } else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(parsedColor ?: Color(0xFF94A3B8))
                .border(1.dp, colors.cardBorder, CircleShape)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                color = colors.textPrimary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(colors.accent),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Enter custom hex (#0284C7)",
                        color = colors.textSecondary.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
                innerTextField()
            }
        )

        Button(
            onClick = {
                val trimmed = value.trim()
                val hex = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
                if (parsedColor != null) {
                    onApplyHex(hex)
                }
            },
            enabled = parsedColor != null,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = parsedColor ?: colors.accent,
                disabledContainerColor = colors.iconBackground
            ),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = "Apply",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun LiveSingleAccentPreviewCard(accentColor: Color) {
    val colors = LocalSettingsColors.current
    var switchState by remember { mutableStateOf(true) }
    var sliderState by remember { mutableFloatStateOf(0.65f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Interactive Controls",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }

                Switch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = accentColor,
                        checkedThumbColor = Color.White
                    )
                )
            }

            Slider(
                value = sliderState,
                onValueChange = { sliderState = it },
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor
                )
            )

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    text = "Accent Button Surface",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LiveMultiAccentPreviewCard(
    cardAccent: Color,
    navAccent: Color,
    iconAccent: Color,
    controlAccent: Color,
    buttonAccent: Color
) {
    val colors = LocalSettingsColors.current
    var switchState by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.5.dp, cardAccent.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simulated Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(navAccent.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Navigation Bar (Nav Accent)",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = navAccent
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(navAccent)
                )
            }

            // Controls & Icons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconAccent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SmartButton,
                            contentDescription = null,
                            tint = iconAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Icon & Control",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Distinct component colors",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Switch(
                    checked = switchState,
                    onCheckedChange = { switchState = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = controlAccent,
                        checkedThumbColor = Color.White
                    )
                )
            }

            // Action Button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonAccent)
            ) {
                Text(
                    text = "Action Button (Button Accent)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AppearanceOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val colors = LocalSettingsColors.current
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 3.dp else 1.5.dp,
                shape = cardShape,
                ambientColor = Color(0x14000000),
                spotColor = Color(0x20000000)
            )
            .clip(cardShape)
            .clickable(onClick = onClick),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colors.accent.copy(alpha = 0.12f) else colors.cardBackground
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) colors.accent else colors.cardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) colors.accent.copy(alpha = 0.2f) else colors.iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (selected) colors.accent else colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .then(
                        if (selected) {
                            Modifier.background(colors.accent)
                        } else {
                            Modifier.border(1.dp, colors.cardBorder, RoundedCornerShape(7.dp))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

private fun formatHour(hour: Int): String {
    val h = hour.coerceIn(0, 23)
    val period = if (h < 12) "AM" else "PM"
    val displayH = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else -> h
    }
    val padded24 = if (h < 10) "0$h:00" else "$h:00"
    return "$displayH:00 $period ($padded24)"
}
