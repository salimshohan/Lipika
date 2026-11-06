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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.common.model.ShiftState
import io.lipika.data.layout.PredefinedLayouts
import io.lipika.data.prefs.LipikaPreferences
import io.lipika.feature.ime.ui.KeyboardRootView
import io.lipika.feature.ime.ui.ScaledKeyboardPreview
import io.lipika.theme.LipikaTheme
import io.lipika.theme.ThemeRepository

@Composable
fun AppearanceScreen(
    preferences: LipikaPreferences,
    onUpdateDisplayMode: (String) -> Unit,
    onUpdateAppTheme: (String) -> Unit,
    onUpdateKeyboardThemeMode: (String) -> Unit,
    onUpdateDarkStyle: (String) -> Unit,
    onUpdateThemeSchedule: (String) -> Unit,
    onUpdateCustomScheduleHours: (Int, Int) -> Unit,
    onOpenThemeStudio: () -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalSettingsColors.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        // ==========================================
        // SECTION 1: DISPLAY MODE
        // ==========================================
        AppearanceSection(
            title = "Display Mode",
            description = "Choose whether the overall interface displays in light or dark mode.",
            icon = Icons.Outlined.BrightnessMedium
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppearanceOptionCard(
                    title = "Light",
                    subtitle = "Bright interface with crisp contrast",
                    icon = Icons.Outlined.LightMode,
                    selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_LIGHT,
                    onClick = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_LIGHT) }
                )
                AppearanceOptionCard(
                    title = "Dark",
                    subtitle = "Dark surfaces that reduce eye strain",
                    icon = Icons.Outlined.DarkMode,
                    selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_DARK,
                    onClick = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_DARK) }
                )
                AppearanceOptionCard(
                    title = "System Default",
                    subtitle = "Follow Android system appearance",
                    icon = Icons.Outlined.BrightnessAuto,
                    selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_SYSTEM,
                    onClick = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_SYSTEM) }
                )
            }
        }

        // ==========================================
        // SECTION 2: APP THEME
        // ==========================================
        AppearanceSection(
            title = "App Theme",
            description = "Configure the styling and color palette of the Lipika settings application.",
            icon = Icons.Outlined.ColorLens
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppearanceOptionCard(
                    title = "Light",
                    subtitle = "Clean frosted light aesthetic",
                    icon = Icons.Outlined.WbSunny,
                    selected = preferences.appTheme == LipikaPreferences.APP_THEME_LIGHT,
                    onClick = { onUpdateAppTheme(LipikaPreferences.APP_THEME_LIGHT) }
                )
                AppearanceOptionCard(
                    title = "Dark",
                    subtitle = "Obsidian & frosted night aesthetic",
                    icon = Icons.Outlined.DarkMode,
                    selected = preferences.appTheme == LipikaPreferences.APP_THEME_DARK,
                    onClick = { onUpdateAppTheme(LipikaPreferences.APP_THEME_DARK) }
                )
                AppearanceOptionCard(
                    title = "System Default",
                    subtitle = "Matches system appearance",
                    icon = Icons.Outlined.BrightnessAuto,
                    selected = preferences.appTheme == LipikaPreferences.APP_THEME_SYSTEM,
                    onClick = { onUpdateAppTheme(LipikaPreferences.APP_THEME_SYSTEM) }
                )
                AppearanceOptionCard(
                    title = "Dynamic Color",
                    subtitle = "Harmonizes with your device wallpaper palette",
                    icon = Icons.Outlined.AutoAwesome,
                    selected = preferences.appTheme == LipikaPreferences.APP_THEME_DYNAMIC,
                    onClick = { onUpdateAppTheme(LipikaPreferences.APP_THEME_DYNAMIC) }
                )
            }
        }

        // ==========================================
        // SECTION 3: KEYBOARD THEME (With Live Preview)
        // ==========================================
        AppearanceSection(
            title = "Keyboard Theme",
            description = "Select the theme for your Lipika typing experience, with a live keyboard preview.",
            icon = Icons.Outlined.Palette
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppearanceOptionCard(
                    title = "Light",
                    subtitle = "Crisp white keys with cool slate accents",
                    icon = Icons.Outlined.LightMode,
                    selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_LIGHT,
                    onClick = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_LIGHT) }
                )
                AppearanceOptionCard(
                    title = "Dark",
                    subtitle = "Graphite, AMOLED, or pure black style",
                    icon = Icons.Outlined.DarkMode,
                    selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_DARK,
                    onClick = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_DARK) }
                )
                AppearanceOptionCard(
                    title = "Custom",
                    subtitle = "Use active theme from Theme Studio or custom imports",
                    icon = Icons.Outlined.Palette,
                    selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_CUSTOM,
                    onClick = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_CUSTOM) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Embedded Live Keyboard Preview Card
                val effectiveThemeId = preferences.effectiveKeyboardTheme()
                val activeScheme = remember(context, effectiveThemeId) {
                    ThemeRepository.getInstance(context).getColorScheme(effectiveThemeId)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = Color(0x14000000),
                            spotColor = Color(0x20000000)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Live Keyboard Preview",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Theme: ${effectiveThemeId.replace('_', ' ').split(' ').joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }}",
                                    fontSize = 12.sp,
                                    color = colors.accent
                                )
                            }
                            TextButton(onClick = onOpenThemeStudio) {
                                Text(
                                    text = "Themes & Studio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.accent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive live preview box
                        LipikaTheme(colorScheme = activeScheme) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                                    .background(activeScheme.containerBackground)
                            ) {
                                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                                val baseWidth = if (screenWidth > 500.dp) 380.dp else screenWidth
                                val fullRowHeight = if (preferences.showNumberRow) 50.dp else 56.dp
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
                                        showKeyBorders = preferences.showKeyBorders,
                                        showKeyShadows = preferences.showKeyShadows,
                                        defaultShowNumberRow = preferences.showNumberRow,
                                        defaultShowSecondaryKeys = false,
                                        initialRowHeight = targetRowHeight,
                                        heightScalePortrait = preferences.keyboardHeightScale,
                                        applySystemInsets = false,
                                        isInteractive = false,
                                        navBarBottomInset = 0.dp
                                    )
                                }

                                // Non-touchable overlay
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
                    }
                }
            }
        }

        // ==========================================
        // SECTION 4: DARK STYLE
        // ==========================================
        AppearanceSection(
            title = "Dark Style",
            description = "Choose how dark theme elements and keyboards render when dark mode is active.",
            icon = Icons.Outlined.DarkMode
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppearanceOptionCard(
                    title = "Pure Black",
                    subtitle = "#000000 pitch black for maximum battery savings",
                    colorDot = Color(0xFF000000),
                    selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_PURE_BLACK,
                    onClick = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_PURE_BLACK) }
                )
                AppearanceOptionCard(
                    title = "AMOLED",
                    subtitle = "Deep black background with dark charcoal keycaps",
                    colorDot = Color(0xFF18181B),
                    selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_AMOLED,
                    onClick = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_AMOLED) }
                )
                AppearanceOptionCard(
                    title = "Dark Gray",
                    subtitle = "Soft balanced graphite gray for comfortable reading",
                    colorDot = Color(0xFF22242B),
                    selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_DARK_GRAY,
                    onClick = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_DARK_GRAY) }
                )
            }
        }

        // ==========================================
        // SECTION 5: SCHEDULE
        // ==========================================
        AppearanceSection(
            title = "Schedule",
            description = "Automatically transition between light and dark themes based on your preference.",
            icon = Icons.Outlined.Schedule
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppearanceOptionCard(
                    title = "Always Light",
                    subtitle = "Keep theme in light mode at all times",
                    icon = Icons.Outlined.WbSunny,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_LIGHT,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_LIGHT) }
                )
                AppearanceOptionCard(
                    title = "Always Dark",
                    subtitle = "Keep theme in dark mode at all times",
                    icon = Icons.Outlined.DarkMode,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_DARK,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_DARK) }
                )
                AppearanceOptionCard(
                    title = "Sunset to Sunrise",
                    subtitle = "Automatically dark from 6:00 PM to 6:00 AM",
                    icon = Icons.Outlined.WbTwilight,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE) }
                )
                AppearanceOptionCard(
                    title = "Custom Schedule",
                    subtitle = "Define specific hours for dark mode activation",
                    icon = Icons.Outlined.Schedule,
                    selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM,
                    onClick = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_CUSTOM) }
                )

                // Custom Schedule Expandable Configuration Card
                AnimatedVisibility(
                    visible = preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color(0x14000000),
                                spotColor = Color(0x20000000)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Custom Hours Configuration",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )

                            // Start Hour Slider
                            var startHourFloat by remember(preferences.customScheduleStartHour) {
                                mutableFloatStateOf(preferences.customScheduleStartHour.toFloat())
                            }
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Dark Mode Starts",
                                        fontSize = 13.sp,
                                        color = colors.textSecondary
                                    )
                                    Text(
                                        text = formatHour(startHourFloat.toInt()),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accent
                                    )
                                }
                                Slider(
                                    value = startHourFloat,
                                    onValueChange = { startHourFloat = it },
                                    onValueChangeFinished = {
                                        onUpdateCustomScheduleHours(startHourFloat.toInt(), preferences.customScheduleEndHour)
                                    },
                                    valueRange = 0f..23f,
                                    steps = 22,
                                    colors = SliderDefaults.colors(
                                        thumbColor = colors.accent,
                                        activeTrackColor = colors.accent,
                                        inactiveTrackColor = colors.cardBorder
                                    )
                                )
                            }

                            // End Hour Slider
                            var endHourFloat by remember(preferences.customScheduleEndHour) {
                                mutableFloatStateOf(preferences.customScheduleEndHour.toFloat())
                            }
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Dark Mode Ends",
                                        fontSize = 13.sp,
                                        color = colors.textSecondary
                                    )
                                    Text(
                                        text = formatHour(endHourFloat.toInt()),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accent
                                    )
                                }
                                Slider(
                                    value = endHourFloat,
                                    onValueChange = { endHourFloat = it },
                                    onValueChangeFinished = {
                                        onUpdateCustomScheduleHours(preferences.customScheduleStartHour, endHourFloat.toInt())
                                    },
                                    valueRange = 0f..23f,
                                    steps = 22,
                                    colors = SliderDefaults.colors(
                                        thumbColor = colors.accent,
                                        activeTrackColor = colors.accent,
                                        inactiveTrackColor = colors.cardBorder
                                    )
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
private fun AppearanceOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    colorDot: Color? = null,
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
            // Leading Icon or Color dot badge
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
            } else if (colorDot != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorDot)
                        .border(1.dp, if (selected) colors.accent else colors.cardBorder, RoundedCornerShape(12.dp))
                )
            }

            // Labels
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

            // Trailing Rectangular Badge Indicator (NO circular radio buttons!)
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
