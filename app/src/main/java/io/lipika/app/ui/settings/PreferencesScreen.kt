package io.lipika.app.ui.settings

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
import io.lipika.data.prefs.LipikaPreferences
import kotlin.math.roundToInt

@Composable
fun PreferencesScreen(
    preferences: LipikaPreferences,
    onUpdateVibrate: (Boolean) -> Unit = {},
    onUpdateVibrationType: (String) -> Unit = {},
    onUpdateVibrationStrength: (Int) -> Unit = {},
    onUpdateSound: (Boolean) -> Unit = {},
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
    val strings = LocalLipikaStrings.current
    val isBn = preferences.appLanguage == LipikaPreferences.APP_LANG_BN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = if (isBn) "কী প্রদর্শন ও পপআপ" else "Key Display & Popups")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = strings.numberRowTitle,
                subtitle = strings.numberRowSubtitle,
                checked = preferences.showNumberRow,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateShowNumberRow
            )

            SettingsToggleItem(
                title = if (isBn) "সেকেন্ডারি প্রতীক হিন্টস" else "Secondary Key Hints",
                subtitle = if (isBn) "কী-ক্যাপের কোণায় সেকেন্ডারি প্রতীকের ইঙ্গিত প্রদর্শন করুন" else "Show secondary symbol hints on the corner of keycaps",
                checked = preferences.showSecondaryKeys,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateShowSecondaryKeys
            )

            SettingsToggleItem(
                title = strings.keyPopupsTitle,
                subtitle = strings.keyPopupsSubtitle,
                checked = preferences.showKeyPopups,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateShowKeyPopups
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = if (isBn) "স্পর্শ ও টাইমিং" else "Key Touch & Timing")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            val delayMs = preferences.longPressDelayMs
            val delayLabel = if (isBn) "$delayMs মি.সে." else "$delayMs ms"

            SettingsSliderItem(
                title = strings.longPressDelayTitle,
                subtitle = strings.longPressDelaySubtitle,
                value = delayMs.toFloat().coerceIn(100f, 500f),
                valueRange = 100f..500f,
                steps = 0,
                valueLabel = delayLabel,
                position = GroupPosition.TOP,
                onValueChange = { onUpdateLongPressDelay(it.roundToInt().coerceIn(100, 500)) }
            )

            // Quick preset pills for 100 to 500 ms
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(100, 150, 200, 250, 300, 400, 500).forEach { preset ->
                    val isSelected = delayMs == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colors.accent else colors.inputBackground)
                            .border(
                                1.dp,
                                if (isSelected) colors.accent else colors.cardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdateLongPressDelay(preset) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$preset",
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else colors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        SettingsCategoryHeader(title = if (isBn) "স্মার্ট জেসচার" else "Smart Gestures")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsRadioOption(
                title = if (isBn) "সোয়াইপ ডিলিট: শব্দ ধরে ধরে" else "Swipe Delete (Selective): By Word",
                subtitle = if (isBn) "ব্যাকস্পেস থেকে বামে টানলে শব্দ অনুসারে টেক্সট সিলেক্ট ও ডিলিট হবে" else "Swiping left from backspace selects and deletes text word-by-word",
                selected = preferences.selectiveDeleteMode == LipikaPreferences.DELETE_MODE_WORD,
                position = GroupPosition.TOP,
                onSelect = { onUpdateSelectiveDeleteMode(LipikaPreferences.DELETE_MODE_WORD) }
            )

            SettingsRadioOption(
                title = if (isBn) "সোয়াইপ ডিলিট: অক্ষর ধরে ধরে" else "Swipe Delete (Selective): By Character",
                subtitle = if (isBn) "ব্যাকস্পেস থেকে বামে টানলে অক্ষর অনুসারে টেক্সট সিলেক্ট ও ডিলিট হবে" else "Swiping left from backspace selects and deletes text character-by-character",
                selected = preferences.selectiveDeleteMode == LipikaPreferences.DELETE_MODE_CHARACTER,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateSelectiveDeleteMode(LipikaPreferences.DELETE_MODE_CHARACTER) }
            )

            SettingsToggleItem(
                title = strings.cursorTrackpadTitle,
                subtitle = strings.cursorTrackpadSubtitle,
                checked = preferences.cursorTrackpadEnabled,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateCursorTrackpad
            )
        }
    }
}
