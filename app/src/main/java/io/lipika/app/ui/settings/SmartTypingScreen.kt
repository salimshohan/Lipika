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
    var showPersonalDictionaryDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colors = LocalSettingsColors.current

    // 1. Personal Dictionary Management Dialog
    if (showPersonalDictionaryDialog) {
        val wordList = remember { mutableStateListOf<String>().apply { addAll(getLearnedWords()) } }
        var newWordInput by remember { mutableStateOf("") }
        var searchQuery by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showPersonalDictionaryDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.dialogBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Personal Dictionary",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Manage custom words & vocabulary",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                        IconButton(onClick = { showPersonalDictionaryDialog = false }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Close", tint = colors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Add new custom word row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newWordInput,
                            onValueChange = { newWordInput = it },
                            placeholder = { Text("Add new custom word...", fontSize = 13.sp, color = colors.textSecondary) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colors.inputBackground,
                                unfocusedContainerColor = colors.inputBackground,
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.inputBorder,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary
                            )
                        )
                        Button(
                            onClick = {
                                val trimmed = newWordInput.trim()
                                if (trimmed.length >= 2) {
                                    onAddCustomWord(trimmed)
                                    if (!wordList.contains(trimmed)) {
                                        wordList.add(0, trimmed)
                                    }
                                    newWordInput = ""
                                    Toast.makeText(context, "\"$trimmed\" added to dictionary", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Word must be at least 2 characters", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search filter
                    if (wordList.isNotEmpty()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Filter words...", fontSize = 12.sp, color = colors.textSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Outlined.Search, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colors.inputBackground,
                                unfocusedContainerColor = colors.inputBackground,
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.cardBorder,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Word List
                    val filteredWords = if (searchQuery.isBlank()) {
                        wordList
                    } else {
                        wordList.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.inputBackground)
                            .padding(8.dp)
                    ) {
                        if (filteredWords.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (wordList.isEmpty()) "No custom words yet.\nAdd words above to personalize your vocabulary." else "No matching words found.",
                                    color = colors.textSecondary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredWords, key = { it }) { word ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = word,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.textPrimary
                                        )
                                        IconButton(
                                            onClick = {
                                                onDeleteCustomWord(word)
                                                wordList.remove(word)
                                                Toast.makeText(context, "Deleted \"$word\"", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFFF5555).copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dialog bottom row: Delete all learned data & Done
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                showPersonalDictionaryDialog = false
                                showDeleteAllDialog = true
                            }
                        ) {
                            Text("Clear All Data", color = Color(0xFFFF5555), fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showPersonalDictionaryDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                        ) {
                            Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    // 2. Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(
                    text = "Clear All Learned Words?",
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "This will permanently erase all custom words, next-word prediction n-grams, and personalized frequency data learned from your typing.",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAllDialog = false
                        onDeleteLearnedWords()
                        Toast.makeText(context, "All learned words erased", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = Color(0xFFFF5555), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.dialogBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

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
        // CATEGORY C: Typing Intelligence
        // ==========================================
        SettingsCategoryHeader(title = "Typing Intelligence")

        SettingsGroup(modifier = Modifier.padding(horizontal = 16.dp)) {
            SettingsToggleItem(
                title = "Smart Spell Checker",
                subtitle = "Detect spelling mistakes and suggest correct alternatives.",
                checked = preferences.smartSpellChecker,
                position = GroupPosition.TOP,
                onCheckedChange = onUpdateSmartSpellChecker
            )

            SettingsNavigationItem(
                title = "Personal Dictionary",
                subtitle = "Manage your custom words and personal vocabulary.",
                position = GroupPosition.MIDDLE,
                onClick = { showPersonalDictionaryDialog = true }
            )

            SettingsToggleItem(
                title = "Custom Word Learning",
                subtitle = "Learn frequently used words to improve future suggestions.",
                checked = preferences.customWordLearning,
                position = GroupPosition.BOTTOM,
                onCheckedChange = onUpdateCustomWordLearning
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // CATEGORY D: Glide & Gesture Typing
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
