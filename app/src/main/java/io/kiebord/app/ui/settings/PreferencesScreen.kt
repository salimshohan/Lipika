package io.kiebord.app.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kiebord.data.prefs.KiebordPreferences
import kotlin.math.roundToInt

@Composable
fun PreferencesScreen(
    preferences: KiebordPreferences,
    onUpdateVibrate: (Boolean) -> Unit,
    onUpdateVibrationType: (String) -> Unit = {},
    onUpdateVibrationStrength: (Int) -> Unit,
    onUpdateSound: (Boolean) -> Unit,
    onUpdateLongPressDelay: (Int) -> Unit,
    onUpdateAccentColor: (String) -> Unit = {},
    onUpdateShowNumberRow: (Boolean) -> Unit,
    onUpdateShowKeyPopups: (Boolean) -> Unit,
    onUpdateShowSecondaryKeys: (Boolean) -> Unit = {},
    onUpdateCursorTrackpad: (Boolean) -> Unit,
    onUpdateOneHandedMode: (String) -> Unit,
    onUpdateLongPressDelete: (Boolean) -> Unit = {},
    onUpdateSelectiveDeleteMode: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val colors = LocalSettingsColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // 1. Dynamic Accent Color Customization
        SettingsCategoryHeader(title = "App Accent Color")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsItemCard(position = GroupPosition.ALONE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = "Accent",
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dynamic Accent Theme",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Personalize colors across controls, toggles & sliders",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val accentPresets = listOf(
                        Triple(KiebordPreferences.ACCENT_CYAN, "Cyan", Color(0xFF06B6D4)),
                        Triple(KiebordPreferences.ACCENT_EMERALD, "Emerald", Color(0xFF10B981)),
                        Triple(KiebordPreferences.ACCENT_ORANGE, "Orange", Color(0xFFF97316)),
                        Triple(KiebordPreferences.ACCENT_VIOLET, "Violet", Color(0xFF8B5CF6)),
                        Triple(KiebordPreferences.ACCENT_ROSE, "Rose", Color(0xFFEC4899)),
                        Triple(KiebordPreferences.ACCENT_AMBER, "Amber", Color(0xFFF59E0B))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        accentPresets.forEach { (key, name, color) ->
                            val isSelected = preferences.accentColor == key

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onUpdateAccentColor(key) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
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
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) colors.accent else colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Haptic & Acoustic Feedback
        SettingsCategoryHeader(title = "Haptic & Acoustic Feedback")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = "Vibrate on Keypress",
                subtitle = "Subtle tactile feedback on key taps",
                checked = preferences.vibrateOnKeypress,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateVibrate
            )

            if (preferences.vibrateOnKeypress) {
                // Feedback Type Selector: Haptic vs Vibration
                SettingsItemCard(position = GroupPosition.MIDDLE) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isHaptic = preferences.vibrationType == KiebordPreferences.VIBRATION_TYPE_HAPTIC
                        val hapticBg = if (isHaptic) colors.accent.copy(alpha = 0.15f) else colors.inputBackground
                        val hapticBorder = if (isHaptic) colors.accent else colors.inputBorder
                        val hapticTint = if (isHaptic) colors.accent else colors.textSecondary
                        val hapticText = if (isHaptic) colors.accent else colors.textPrimary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(hapticBg)
                                .border(1.dp, hapticBorder, RoundedCornerShape(10.dp))
                                .clickable { onUpdateVibrationType(KiebordPreferences.VIBRATION_TYPE_HAPTIC) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TouchApp,
                                    contentDescription = null,
                                    tint = hapticTint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Haptic",
                                    fontSize = 13.sp,
                                    fontWeight = if (isHaptic) FontWeight.Bold else FontWeight.Medium,
                                    color = hapticText
                                )
                            }
                        }

                        val isVibration = preferences.vibrationType == KiebordPreferences.VIBRATION_TYPE_VIBRATION
                        val vibrationBg = if (isVibration) colors.accent.copy(alpha = 0.15f) else colors.inputBackground
                        val vibrationBorder = if (isVibration) colors.accent else colors.inputBorder
                        val vibrationTint = if (isVibration) colors.accent else colors.textSecondary
                        val vibrationText = if (isVibration) colors.accent else colors.textPrimary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(vibrationBg)
                                .border(1.dp, vibrationBorder, RoundedCornerShape(10.dp))
                                .clickable { onUpdateVibrationType(KiebordPreferences.VIBRATION_TYPE_VIBRATION) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Vibration,
                                    contentDescription = null,
                                    tint = vibrationTint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Vibration",
                                    fontSize = 13.sp,
                                    fontWeight = if (isVibration) FontWeight.Bold else FontWeight.Medium,
                                    color = vibrationText
                                )
                            }
                        }
                    }
                }

                if (preferences.vibrationType == KiebordPreferences.VIBRATION_TYPE_VIBRATION) {
                    val strength = preferences.vibrationStrength
                    val strengthLabel = when {
                        strength <= 25 -> "Light ($strength%)"
                        strength <= 65 -> "Medium ($strength%)"
                        else -> "Strong ($strength%)"
                    }

                    SettingsSliderItem(
                        title = "Vibration Intensity",
                        subtitle = "Motor vibration pulse strength",
                        value = strength.toFloat(),
                        valueRange = 10f..100f,
                        steps = 8,
                        valueLabel = strengthLabel,
                        position = GroupPosition.MIDDLE,
                        onValueChange = { onUpdateVibrationStrength(it.toInt()) }
                    )
                }
            }

            SettingsToggleItem(
                title = "Sound on Keypress",
                subtitle = "Audible click feedback when typing",
                checked = preferences.soundOnKeypress,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateSound
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = "Key Display & Popups")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = "Dedicated Number Row",
                subtitle = "Display persistent 5th row of digits above letter keys",
                checked = preferences.showNumberRow,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateShowNumberRow
            )

            SettingsToggleItem(
                title = "Secondary Key Hints",
                subtitle = "Show secondary symbol hints on the corner of keycaps",
                checked = preferences.showSecondaryKeys,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateShowSecondaryKeys
            )

            SettingsToggleItem(
                title = "Popup on Keypress",
                subtitle = "Show circular character preview popup when touching keys",
                checked = preferences.showKeyPopups,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateShowKeyPopups
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = "Key Touch & Timing")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            val delayMs = preferences.longPressDelayMs
            val delayLabel = "$delayMs ms"

            SettingsSliderItem(
                title = "Long-Press Delay",
                subtitle = "Hold duration required to trigger more-keys and symbols (100–600 ms)",
                value = delayMs.toFloat().coerceIn(100f, 600f),
                valueRange = 100f..600f,
                steps = 0,
                valueLabel = delayLabel,
                position = GroupPosition.ALONE,
                onValueChange = { onUpdateLongPressDelay(it.roundToInt()) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = "Smart Gestures")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = "Swipe Delete (Selective): By Word",
                subtitle = "Swiping left from backspace selects and deletes text word-by-word",
                selected = preferences.selectiveDeleteMode == KiebordPreferences.DELETE_MODE_WORD,
                position = GroupPosition.TOP,
                onSelect = { onUpdateSelectiveDeleteMode(KiebordPreferences.DELETE_MODE_WORD) }
            )

            SettingsRadioOption(
                title = "Swipe Delete (Selective): By Character",
                subtitle = "Swiping left from backspace selects and deletes text character-by-character",
                selected = preferences.selectiveDeleteMode == KiebordPreferences.DELETE_MODE_CHARACTER,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateSelectiveDeleteMode(KiebordPreferences.DELETE_MODE_CHARACTER) }
            )

            SettingsToggleItem(
                title = "Spacebar Cursor Trackpad",
                subtitle = "Slide thumb horizontally across spacebar to position cursor",
                checked = preferences.cursorTrackpadEnabled,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateCursorTrackpad
            )
        }
    }
}
