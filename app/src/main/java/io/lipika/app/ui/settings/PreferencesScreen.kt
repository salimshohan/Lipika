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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
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
                subtitle = "Hold duration required to trigger more-keys and symbols (100–500 ms)",
                value = delayMs.toFloat().coerceIn(100f, 500f),
                valueRange = 100f..500f,
                steps = 40,
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
                selected = preferences.selectiveDeleteMode == LipikaPreferences.DELETE_MODE_WORD,
                position = GroupPosition.TOP,
                onSelect = { onUpdateSelectiveDeleteMode(LipikaPreferences.DELETE_MODE_WORD) }
            )

            SettingsRadioOption(
                title = "Swipe Delete (Selective): By Character",
                subtitle = "Swiping left from backspace selects and deletes text character-by-character",
                selected = preferences.selectiveDeleteMode == LipikaPreferences.DELETE_MODE_CHARACTER,
                position = GroupPosition.MIDDLE,
                onSelect = { onUpdateSelectiveDeleteMode(LipikaPreferences.DELETE_MODE_CHARACTER) }
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
