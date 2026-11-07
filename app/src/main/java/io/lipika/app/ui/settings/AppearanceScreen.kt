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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
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
    AccentPreset(LipikaPreferences.ACCENT_PURPLE, "Purple", Color(0xFF7C3AED), "#7C3AED"),
    AccentPreset(LipikaPreferences.ACCENT_LAVENDER, "Lavender", Color(0xFF8B5CF6), "#8B5CF6"),
    AccentPreset(LipikaPreferences.ACCENT_ROSE, "Rose", Color(0xFFF43F5E), "#F43F5E"),
    AccentPreset(LipikaPreferences.ACCENT_CORAL, "Coral Sunset", Color(0xFFF97316), "#F97316"),
    AccentPreset(LipikaPreferences.ACCENT_NEUTRAL_SLATE, "Slate Grey", Color(0xFF475569), "#475569")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 36.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ==========================================
        // 1. DISPLAY MODE
        // ==========================================
        SettingsCategoryHeader(title = "Display Mode")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "System Default",
                subtitle = "Follow current Android system night mode",
                selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_SYSTEM,
                position = GroupPosition.TOP,
                onSelect = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_SYSTEM) }
            )
            SettingsRadioOption(
                title = "Light",
                subtitle = "Bright clean background and high contrast",
                selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_LIGHT,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_LIGHT) }
            )
            SettingsRadioOption(
                title = "Dark",
                subtitle = "Comfortable dark interface for low-light environments",
                selected = preferences.displayMode == LipikaPreferences.DISPLAY_MODE_DARK,
                position = GroupPosition.BOTTOM,
                onSelect = { onUpdateDisplayMode(LipikaPreferences.DISPLAY_MODE_DARK) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 2. APP THEME
        // ==========================================
        SettingsCategoryHeader(title = "App Theme")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "System Default",
                subtitle = "Adapt automatically to system theme changes",
                selected = preferences.appTheme == LipikaPreferences.APP_THEME_SYSTEM,
                position = GroupPosition.TOP,
                onSelect = { onUpdateAppTheme(LipikaPreferences.APP_THEME_SYSTEM) }
            )
            SettingsRadioOption(
                title = "Light",
                subtitle = "Always maintain light aesthetic throughout the app",
                selected = preferences.appTheme == LipikaPreferences.APP_THEME_LIGHT,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateAppTheme(LipikaPreferences.APP_THEME_LIGHT) }
            )
            SettingsRadioOption(
                title = "Dark",
                subtitle = "Always maintain dark frosted glass appearance",
                selected = preferences.appTheme == LipikaPreferences.APP_THEME_DARK,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateAppTheme(LipikaPreferences.APP_THEME_DARK) }
            )
            SettingsRadioOption(
                title = "Dynamic Color",
                subtitle = "Extract Material You wallpaper colors (Android 12+)",
                selected = preferences.appTheme == LipikaPreferences.APP_THEME_DYNAMIC,
                position = GroupPosition.BOTTOM,
                onSelect = { onUpdateAppTheme(LipikaPreferences.APP_THEME_DYNAMIC) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 3. KEYBOARD THEME MODE
        // ==========================================
        SettingsCategoryHeader(title = "Keyboard Theme")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "Dark",
                subtitle = "Deep dark keyboard theme with high contrast labels",
                selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_DARK,
                position = GroupPosition.TOP,
                onSelect = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_DARK) }
            )
            SettingsRadioOption(
                title = "Light",
                subtitle = "Clean bright keys with crisp legible typography",
                selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_LIGHT,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_LIGHT) }
            )
            SettingsRadioOption(
                title = "Custom",
                subtitle = "Use your custom designed theme from Theme Studio",
                selected = preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_CUSTOM,
                position = GroupPosition.BOTTOM,
                onSelect = { onUpdateKeyboardThemeMode(LipikaPreferences.KEYBOARD_THEME_CUSTOM) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 4. DARK STYLE
        // ==========================================
        SettingsCategoryHeader(title = "Dark Style")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "Dark Gray",
                subtitle = "Standard soft dark with comfortable elevation",
                selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_DARK_GRAY,
                position = GroupPosition.TOP,
                onSelect = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_DARK_GRAY) }
            )
            SettingsRadioOption(
                title = "AMOLED",
                subtitle = "Ultra deep black tailored for OLED power saving",
                selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_AMOLED,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_AMOLED) }
            )
            SettingsRadioOption(
                title = "Pure Black",
                subtitle = "#000000 true pitch black background",
                selected = preferences.darkStyle == LipikaPreferences.DARK_STYLE_PURE_BLACK,
                position = GroupPosition.BOTTOM,
                onSelect = { onUpdateDarkStyle(LipikaPreferences.DARK_STYLE_PURE_BLACK) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 5. THEME SCHEDULE
        // ==========================================
        SettingsCategoryHeader(title = "Theme Schedule")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "System Default",
                subtitle = "Switch dark/light mode following OS schedule",
                selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_SYSTEM_DEFAULT,
                position = GroupPosition.TOP,
                onSelect = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_SYSTEM_DEFAULT) }
            )
            SettingsRadioOption(
                title = "Always Light",
                subtitle = "Never switch to dark mode automatically",
                selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_LIGHT,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_LIGHT) }
            )
            SettingsRadioOption(
                title = "Always Dark",
                subtitle = "Keep dark mode active day and night",
                selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_ALWAYS_DARK,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_ALWAYS_DARK) }
            )
            SettingsRadioOption(
                title = "Sunset to Sunrise",
                subtitle = "Turn on dark mode automatically at 6:00 PM",
                selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_SUNSET_TO_SUNRISE) }
            )
            SettingsRadioOption(
                title = "Custom Schedule",
                subtitle = "Specify your own active start and end hours",
                selected = preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM,
                position = if (preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM) GroupPosition.MIDDLE else GroupPosition.BOTTOM,
                onSelect = { onUpdateThemeSchedule(LipikaPreferences.SCHEDULE_CUSTOM) }
            )

            if (preferences.themeSchedule == LipikaPreferences.SCHEDULE_CUSTOM) {
                SettingsItemCard(position = GroupPosition.BOTTOM) {
                    var startHour by remember(preferences.customScheduleStartHour) {
                        mutableFloatStateOf(preferences.customScheduleStartHour.toFloat())
                    }
                    var endHour by remember(preferences.customScheduleEndHour) {
                        mutableFloatStateOf(preferences.customScheduleEndHour.toFloat())
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Start: ${startHour.toInt()}:00",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "End: ${endHour.toInt()}:00",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Slider(
                            value = startHour,
                            onValueChange = {
                                startHour = it
                                onUpdateCustomScheduleHours(it.toInt(), endHour.toInt())
                            },
                            valueRange = 0f..23f,
                            steps = 22,
                            colors = SliderDefaults.colors(
                                thumbColor = colors.accent,
                                activeTrackColor = colors.accent,
                                inactiveTrackColor = colors.inputBorder
                            )
                        )

                        Slider(
                            value = endHour,
                            onValueChange = {
                                endHour = it
                                onUpdateCustomScheduleHours(startHour.toInt(), it.toInt())
                            },
                            valueRange = 0f..23f,
                            steps = 22,
                            colors = SliderDefaults.colors(
                                thumbColor = colors.accent,
                                activeTrackColor = colors.accent,
                                inactiveTrackColor = colors.inputBorder
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // 6. ACCENT COLOR (10 Curated Presets)
        // ==========================================
        SettingsCategoryHeader(title = "Accent Color")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsItemCard(position = GroupPosition.ALONE) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Curated Single Accent",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Applies clean uniform accent highlights to toggles, icons, and buttons",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // 10 Presets in 2 Columns of 5
                    for (chunk in CuratedAccentPresets.chunked(2)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (preset in chunk) {
                                val isSelected = preferences.accentColor == preset.key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) preset.color.copy(alpha = 0.15f) else colors.inputBackground)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) preset.color else colors.inputBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onUpdateAccentMode(LipikaPreferences.ACCENT_MODE_SINGLE)
                                            onUpdateAccentColor(preset.key)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(preset.color)
                                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = preset.name,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = colors.textPrimary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = preset.hexCode,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = colors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
