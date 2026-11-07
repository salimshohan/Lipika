package io.lipika.app.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.lipika.data.prefs.LipikaPreferences

@Composable
fun SmartTypingScreen(
    preferences: LipikaPreferences,
    onUpdateSuggestionsEnabled: (Boolean) -> Unit,
    onUpdateNextWordPrediction: (Boolean) -> Unit,
    onUpdateEmojiPredictionEnabled: (Boolean) -> Unit,
    onUpdateLearnedWordsEnabled: (Boolean) -> Unit,
    onUpdateFilterOffensiveWords: (Boolean) -> Unit,
    onUpdateAutoCorrection: (Boolean) -> Unit,
    onUpdatePhoneticAutoCorrect: (Boolean) -> Unit,
    onUpdateSmartSpellChecker: (Boolean) -> Unit,
    onUpdateCustomWordLearning: (Boolean) -> Unit,
    onAddCustomWord: (String) -> Unit = {},
    onDeleteCustomWord: (String) -> Unit = {},
    onDeleteLearnedWords: () -> Unit = {},
    getLearnedWords: () -> List<String> = { emptyList() },
    onUpdateBengaliSmartPhonetic: (Boolean) -> Unit = {},
    onUpdateBengaliShowRawInputCandidate: (Boolean) -> Unit = {},
    onUpdateGestureTyping: (Boolean) -> Unit = {},
    onUpdateShowGestureTrail: (Boolean) -> Unit = {},
    onUpdateAutoCapitalization: (Boolean) -> Unit = {},
    onUpdateDoubleSpacePeriod: (Boolean) -> Unit = {},
    onUpdateAutoSpaceAfterSuggestion: (Boolean) -> Unit = {},
    onUpdateAutoSpaceAfterPunctuation: (Boolean) -> Unit = {},
    onUpdateRemoveSpaceBeforePunctuation: (Boolean) -> Unit = {},
    onUpdateSuggestionBarMode: (String) -> Unit = {},
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
        // ==========================================
        // CATEGORY A: Suggestions & Prediction
        // ==========================================
        SettingsCategoryHeader(title = "Suggestions & Prediction")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            val stripPosition = if (preferences.suggestionsEnabled) GroupPosition.TOP else GroupPosition.ALONE
            SettingsToggleItem(
                title = "Word Suggestions",
                subtitle = "Show relevant word suggestions while typing.",
                checked = preferences.suggestionsEnabled,
                position = stripPosition,
                onCheckedChange = onUpdateSuggestionsEnabled
            )

            if (preferences.suggestionsEnabled) {
                SettingsToggleItem(
                    title = "Next-Word Prediction",
                    subtitle = "Predict your next word based on previous words and context.",
                    checked = preferences.nextWordPrediction,
                    position = GroupPosition.MIDDLE,
                    onCheckedChange = onUpdateNextWordPrediction
                )

                SettingsToggleItem(
                    title = "Emoji Prediction",
                    subtitle = "Suggest relevant emojis based on the words and context you type.",
                    checked = preferences.emojiPredictionEnabled,
                    position = GroupPosition.MIDDLE,
                    onCheckedChange = onUpdateEmojiPredictionEnabled
                )

                SettingsToggleItem(
                    title = "Personalized Learning",
                    subtitle = "Learn your typing habits to provide more personalized suggestions.",
                    checked = preferences.learnedWordsEnabled,
                    position = GroupPosition.MIDDLE,
                    onCheckedChange = onUpdateLearnedWordsEnabled
                )

                SettingsToggleItem(
                    title = "Show English Input Candidate",
                    subtitle = "Display raw typed English word as first candidate in Borno Phonetic layout",
                    checked = preferences.bengaliShowRawInputCandidate,
                    position = GroupPosition.MIDDLE,
                    onCheckedChange = onUpdateBengaliShowRawInputCandidate
                )

                SettingsToggleItem(
                    title = "Borno Phonetic Proximity Suggestions",
                    subtitle = "Suggest dictionary words and proximity corrections while typing in Borno Phonetic",
                    checked = preferences.bengaliSmartPhonetic,
                    position = GroupPosition.BOTTOM,
                    onCheckedChange = onUpdateBengaliSmartPhonetic
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // CATEGORY B: Text Correction
        // ==========================================
        SettingsCategoryHeader(title = "Text Correction")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = "Filter Offensive Words",
                subtitle = "Hide potentially offensive words from the suggestion bar.",
                checked = preferences.filterOffensiveWords,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateFilterOffensiveWords
            )

            SettingsToggleItem(
                title = "Auto-Correction",
                subtitle = "Automatically correct misspelled words as you type.",
                checked = preferences.autoCorrection,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateAutoCorrection
            )

            SettingsToggleItem(
                title = "Phonetic Correction",
                subtitle = "Fix common phonetic typing mistakes automatically.",
                checked = preferences.phoneticAutoCorrect,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdatePhoneticAutoCorrect
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // CATEGORY C: Glide & Gesture Typing
        // ==========================================
        SettingsCategoryHeader(title = "Glide & Gesture Typing")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            val glidePosition = if (preferences.gestureTypingEnabled) GroupPosition.TOP else GroupPosition.ALONE
            SettingsToggleItem(
                title = "Enable Glide Typing",
                subtitle = "Type words fluidly by swiping finger across keys without lifting",
                icon = Icons.Outlined.Gesture,
                checked = preferences.gestureTypingEnabled,
                position = glidePosition,
                onCheckedChange = onUpdateGestureTyping
            )

            if (preferences.gestureTypingEnabled) {
                SettingsToggleItem(
                    title = "Show Gesture Vapor Trail",
                    subtitle = "Draw glowing dynamic comet trail tracing your glide trajectory",
                    icon = Icons.Outlined.Route,
                    checked = preferences.showGestureTrail,
                    position = GroupPosition.BOTTOM,
                    onCheckedChange = onUpdateShowGestureTrail
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // CATEGORY E: Suggestion Bar & Formatting
        // ==========================================
        if (preferences.suggestionsEnabled) {
            SettingsCategoryHeader(title = "Suggestion Bar Mode")

            SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
                SettingsRadioOption(
                    title = "Normal (Default)",
                    subtitle = "Shows candidates with an expand button for the expanded view",
                    selected = preferences.suggestionBarMode == LipikaPreferences.SUGGESTION_BAR_MODE_NORMAL,
                    position = GroupPosition.TOP,
                    onSelect = { onUpdateSuggestionBarMode(LipikaPreferences.SUGGESTION_BAR_MODE_NORMAL) }
                )

                SettingsRadioOption(
                    title = "Scrollable",
                    subtitle = "Candidates can be scrolled horizontally instead of expanded view",
                    selected = preferences.suggestionBarMode == LipikaPreferences.SUGGESTION_BAR_MODE_SCROLLABLE,
                    position = GroupPosition.BOTTOM,
                    onSelect = { onUpdateSuggestionBarMode(LipikaPreferences.SUGGESTION_BAR_MODE_SCROLLABLE) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        SettingsCategoryHeader(title = "Text Corrections & Shortcuts")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = "Auto-Capitalization",
                subtitle = "Automatically capitalize the first letter of each sentence",
                checked = preferences.autoCapitalization,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateAutoCapitalization
            )

            SettingsToggleItem(
                title = "Double-Space Period",
                subtitle = "Quickly double-tap the spacebar to insert a period followed by a space",
                checked = preferences.doubleSpacePeriod,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateDoubleSpacePeriod
            )

            SettingsToggleItem(
                title = "Auto-Space after Suggestions",
                subtitle = "Automatically insert a space after committing a word from the candidate strip",
                checked = preferences.autoSpaceAfterSuggestion,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateAutoSpaceAfterSuggestion
            )

            SettingsToggleItem(
                title = "Auto-Space after Punctuation",
                subtitle = "Automatically insert a space after comma, period, and sentence terminators",
                checked = preferences.autoSpaceAfterPunctuation,
                position = GroupPosition.MIDDLE,
                onCheckedChange = onUpdateAutoSpaceAfterPunctuation
            )

            SettingsToggleItem(
                title = "Remove Space Before Punctuation",
                subtitle = "Automatically remove space before punctuation marks (except URL and code fields)",
                checked = preferences.removeSpaceBeforePunctuation,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateRemoveSpaceBeforePunctuation
            )
        }
    }
}
