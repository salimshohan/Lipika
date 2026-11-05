package io.kiebord.app.ui.settings

import androidx.compose.runtime.Composable
import io.kiebord.data.prefs.KiebordPreferences

/**
 * Backward-compatibility wrapper for SmartTypingScreen.
 */
@Composable
fun CorrectionsSuggestionsScreen(
    preferences: KiebordPreferences,
    onUpdateSuggestionsEnabled: (Boolean) -> Unit,
    onUpdateNextWordPrediction: (Boolean) -> Unit,
    onUpdatePhoneticAutoCorrect: (Boolean) -> Unit = {},
    onUpdateBengaliSmartPhonetic: (Boolean) -> Unit = {},
    onUpdateBengaliShowRawInputCandidate: (Boolean) -> Unit = {},
    onUpdateLearnedWordsEnabled: (Boolean) -> Unit = {},
    onDeleteLearnedWords: () -> Unit = {},
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
    SmartTypingScreen(
        preferences = preferences,
        onUpdateSuggestionsEnabled = onUpdateSuggestionsEnabled,
        onUpdateNextWordPrediction = onUpdateNextWordPrediction,
        onUpdateEmojiPredictionEnabled = {},
        onUpdateLearnedWordsEnabled = onUpdateLearnedWordsEnabled,
        onUpdateFilterOffensiveWords = {},
        onUpdateAutoCorrection = {},
        onUpdatePhoneticAutoCorrect = onUpdatePhoneticAutoCorrect,
        onUpdateSmartSpellChecker = {},
        onUpdateCustomWordLearning = {},
        onDeleteLearnedWords = onDeleteLearnedWords,
        onUpdateBengaliSmartPhonetic = onUpdateBengaliSmartPhonetic,
        onUpdateBengaliShowRawInputCandidate = onUpdateBengaliShowRawInputCandidate,
        onUpdateGestureTyping = onUpdateGestureTyping,
        onUpdateShowGestureTrail = onUpdateShowGestureTrail,
        onUpdateAutoCapitalization = onUpdateAutoCapitalization,
        onUpdateDoubleSpacePeriod = onUpdateDoubleSpacePeriod,
        onUpdateAutoSpaceAfterSuggestion = onUpdateAutoSpaceAfterSuggestion,
        onUpdateAutoSpaceAfterPunctuation = onUpdateAutoSpaceAfterPunctuation,
        onUpdateRemoveSpaceBeforePunctuation = onUpdateRemoveSpaceBeforePunctuation,
        onUpdateSuggestionBarMode = onUpdateSuggestionBarMode,
        onBack = onBack
    )
}
