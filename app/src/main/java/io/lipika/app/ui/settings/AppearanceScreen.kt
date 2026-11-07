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
    AccentPreset(LipikaPreferences.ACCENT_CYAN, "Electric Cyan", Color(0xFF06B6D4), "#06B6D4"),
    AccentPreset(LipikaPreferences.ACCENT_ROYAL_BLUE, "Royal Blue", Color(0xFF2563EB), "#2563EB"),
    AccentPreset(LipikaPreferences.ACCENT_EMERALD, "Emerald Green", Color(0xFF10B981), "#10B981"),
    AccentPreset(LipikaPreferences.ACCENT_MINT_GREEN, "Neon Lime", Color(0xFF84CC16), "#84CC16"),
    AccentPreset(LipikaPreferences.ACCENT_ORANGE, "Radiant Orange", Color(0xFFF97316), "#F97316"),
    AccentPreset(LipikaPreferences.ACCENT_GOLD, "Golden Amber", Color(0xFFEAB308), "#EAB308"),
    AccentPreset(LipikaPreferences.ACCENT_RED, "Crimson Red", Color(0xFFEF4444), "#EF4444"),
    AccentPreset(LipikaPreferences.ACCENT_PINK, "Hot Pink", Color(0xFFEC4899), "#EC4899"),
    AccentPreset(LipikaPreferences.ACCENT_PURPLE, "Vivid Purple", Color(0xFF7C3AED), "#7C3AED"),
    AccentPreset(LipikaPreferences.ACCENT_VIOLET, "Indigo Blue", Color(0xFF6366F1), "#6366F1")
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
        // 1. THEME SCHEDULE
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
        // 2. ACCENT COLOR (10 Curated Presets)
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
