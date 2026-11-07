package io.lipika.data.prefs

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.lipikaDataStore: DataStore<Preferences> by preferencesDataStore(name = "lipika_settings")

class LipikaPreferencesRepository private constructor(private val context: Context) {

    private val fastCache = context.getSharedPreferences("lipika_fast_cache", Context.MODE_PRIVATE)

    init {
        try {
            val legacyFile = java.io.File(context.filesDir, "datastore/kiebord_settings.preferences_pb")
            val newFile = java.io.File(context.filesDir, "datastore/lipika_settings.preferences_pb")
            if (legacyFile.exists() && !newFile.exists()) {
                legacyFile.copyTo(newFile, overwrite = false)
            }
            val legacyFastCache = context.getSharedPreferences("kiebord_fast_cache", Context.MODE_PRIVATE)
            if (legacyFastCache.all.isNotEmpty() && fastCache.all.isEmpty()) {
                val editor = fastCache.edit()
                for ((k, v) in legacyFastCache.all) {
                    when (v) {
                        is Boolean -> editor.putBoolean(k, v)
                        is Int -> editor.putInt(k, v)
                        is Float -> editor.putFloat(k, v)
                        is Long -> editor.putLong(k, v)
                        is String -> editor.putString(k, v)
                    }
                }
                editor.apply()
            }
        } catch (_: Exception) {}
    }

    fun isSetupCompletedSync(): Boolean {
        return fastCache.getBoolean("setup_completed", false)
    }

    fun getInitialPreferences(): LipikaPreferences {
        return LipikaPreferences(setupCompleted = isSetupCompletedSync())
    }

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("pref_theme")
        val SELECTED_FONT = stringPreferencesKey("pref_selected_font")
        val SELECTED_BENGALI_FONT = stringPreferencesKey("pref_selected_bengali_font")
        val ACCENT_COLOR = stringPreferencesKey("pref_accent_color")
        val ACCENT_MODE = stringPreferencesKey("pref_accent_mode")
        val MULTIPLE_ACCENTS = stringPreferencesKey("pref_multiple_accents")
        val CUSTOM_FONT_PATH = stringPreferencesKey("pref_custom_font_path")
        val KEYBOARD_HEIGHT_SCALE = floatPreferencesKey("pref_keyboard_height_scale")
        val KEYBOARD_HEIGHT_SCALE_LANDSCAPE = floatPreferencesKey("pref_keyboard_height_scale_landscape")
        val CHIN_PADDING_PORTRAIT = floatPreferencesKey("pref_chin_padding_portrait")
        val CHIN_PADDING_LANDSCAPE = floatPreferencesKey("pref_chin_padding_landscape")
        val HORIZONTAL_MARGIN_PORTRAIT = floatPreferencesKey("pref_horizontal_margin_portrait")
        val HORIZONTAL_MARGIN_LANDSCAPE = floatPreferencesKey("pref_horizontal_margin_landscape")
        val SHOW_NUMBER_ROW = booleanPreferencesKey("pref_show_number_row")
        val SHOW_KEY_BORDERS = booleanPreferencesKey("pref_show_key_borders")
        val SHOW_KEY_SHADOWS = booleanPreferencesKey("pref_show_key_shadows")
        val SHOW_KEY_POPUPS = booleanPreferencesKey("pref_show_key_popups")
        val SHOW_SECONDARY_KEYS = booleanPreferencesKey("pref_show_secondary_keys")

        val VIBRATE_ON_KEYPRESS = booleanPreferencesKey("pref_vibrate_on_keypress")
        val VIBRATION_TYPE = stringPreferencesKey("pref_vibration_type")
        val VIBRATION_STRENGTH = intPreferencesKey("pref_vibration_strength")
        val VIBRATION_DURATION_MS = intPreferencesKey("pref_vibration_duration_ms")
        val SOUND_ON_KEYPRESS = booleanPreferencesKey("pref_sound_on_keypress")
        val SOUND_VOLUME = intPreferencesKey("pref_sound_volume")
        val SOUND_PROFILE = stringPreferencesKey("pref_sound_profile")
        val SOUND_DISTINCT_KEYS = booleanPreferencesKey("pref_sound_distinct_keys")
        val VIBRATE_ON_LONG_PRESS = booleanPreferencesKey("pref_vibrate_on_long_press")
        val VIBRATE_ON_SPACE_SWIPE = booleanPreferencesKey("pref_vibrate_on_space_swipe")
        val LONG_PRESS_DELAY_MS = intPreferencesKey("pref_long_press_delay_ms")
        val CURSOR_TRACKPAD_ENABLED = booleanPreferencesKey("pref_cursor_trackpad_enabled")
        val ONE_HANDED_MODE = stringPreferencesKey("pref_one_handed_mode")
        val LONG_PRESS_DELETE_ENABLED = booleanPreferencesKey("pref_long_press_delete_enabled")
        val SELECTIVE_DELETE_MODE = stringPreferencesKey("pref_selective_delete_mode")

        val APP_LANGUAGE = stringPreferencesKey("pref_app_language")
        val PRIMARY_LAYOUT_ID = stringPreferencesKey("pref_primary_layout_id")
        val ENABLED_LAYOUT_IDS = stringSetPreferencesKey("pref_enabled_layout_ids")
        val ENABLED_LAYOUT_ORDER = stringPreferencesKey("pref_enabled_layout_order")

        val SUGGESTIONS_ENABLED = booleanPreferencesKey("pref_suggestions_enabled")
        val NEXT_WORD_PREDICTION = booleanPreferencesKey("pref_next_word_prediction")
        val EMOJI_PREDICTION_ENABLED = booleanPreferencesKey("pref_emoji_prediction_enabled")
        val FILTER_OFFENSIVE_WORDS = booleanPreferencesKey("pref_filter_offensive_words")
        val AUTO_CORRECTION = booleanPreferencesKey("pref_auto_correction")
        val PHONETIC_AUTO_CORRECT = booleanPreferencesKey("pref_phonetic_auto_correct")
        val SMART_SPELL_CHECKER = booleanPreferencesKey("pref_smart_spell_checker")
        val CUSTOM_WORD_LEARNING = booleanPreferencesKey("pref_custom_word_learning")
        val BENGALI_SMART_PHONETIC = booleanPreferencesKey("pref_bengali_smart_phonetic")
        val BENGALI_SHOW_RAW_INPUT_CANDIDATE = booleanPreferencesKey("pref_bengali_show_raw_input_candidate")
        val LEARNED_WORDS_ENABLED = booleanPreferencesKey("pref_learned_words_enabled")
        val GESTURE_TYPING_ENABLED = booleanPreferencesKey("pref_gesture_typing_enabled")
        val SHOW_GESTURE_TRAIL = booleanPreferencesKey("pref_show_gesture_trail")
        val AUTO_CAPITALIZATION = booleanPreferencesKey("pref_auto_capitalization")
        val DOUBLE_SPACE_PERIOD = booleanPreferencesKey("pref_double_space_period")
        val AUTO_SPACE_AFTER_SUGGESTION = booleanPreferencesKey("pref_auto_space_after_suggestion")
        val AUTO_SPACE_AFTER_PUNCTUATION = booleanPreferencesKey("pref_auto_space_after_punctuation")
        val REMOVE_SPACE_BEFORE_PUNCTUATION = booleanPreferencesKey("pref_remove_space_before_punctuation")
        val TRADITIONAL_MODE = booleanPreferencesKey("pref_traditional_mode")
        val SUGGESTION_BAR_MODE = stringPreferencesKey("pref_suggestion_bar_mode")

        val PINNED_TOOLS = stringPreferencesKey("pref_pinned_tools")
        val DRAWER_TOOLS = stringPreferencesKey("pref_drawer_tools")
        val VOICE_TYPING_ENABLED = booleanPreferencesKey("pref_voice_typing_enabled")
        val VOICE_TYPING_PROVIDER = stringPreferencesKey("pref_voice_typing_provider")
        val SETUP_COMPLETED = booleanPreferencesKey("pref_setup_completed")

        // Appearance screen settings
        val DISPLAY_MODE = stringPreferencesKey("pref_display_mode")
        val APP_THEME = stringPreferencesKey("pref_app_theme")
        val KEYBOARD_THEME_MODE = stringPreferencesKey("pref_keyboard_theme_mode")
        val DARK_STYLE = stringPreferencesKey("pref_dark_style")
        val THEME_SCHEDULE = stringPreferencesKey("pref_theme_schedule")
        val CUSTOM_SCHEDULE_START_HOUR = intPreferencesKey("pref_custom_schedule_start_hour")
        val CUSTOM_SCHEDULE_END_HOUR = intPreferencesKey("pref_custom_schedule_end_hour")

        val CLIPBOARD_EXPIRY_HOURS = intPreferencesKey("pref_clipboard_expiry_hours")

        // Typing Statistics
        val STATS_BANGLA_LETTERS = longPreferencesKey("pref_stats_bangla_letters")
        val STATS_ENGLISH_LETTERS = longPreferencesKey("pref_stats_english_letters")
        val STATS_AVRO_WORDS = longPreferencesKey("pref_stats_avro_words")
        val STATS_WORDS_TYPED = longPreferencesKey("pref_stats_words_typed")
        val STATS_NUMBERS_TYPED = longPreferencesKey("pref_stats_numbers_typed")
        val STATS_KEYSTROKES = longPreferencesKey("pref_stats_keystrokes")
        val STATS_AUDIO_CLICKS = longPreferencesKey("pref_stats_audio_clicks")
        val STATS_BACKSPACES = longPreferencesKey("pref_stats_backspaces")
        val STATS_EMOJIS = longPreferencesKey("pref_stats_emojis")
        val STATS_SYMBOLS = longPreferencesKey("pref_stats_symbols")
    }

    val preferencesFlow: Flow<LipikaPreferences> = context.lipikaDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            LipikaPreferences(
                theme = prefs[PreferencesKeys.THEME] ?: LipikaPreferences.THEME_LIPIKA_GRADIENT,
                selectedFont = prefs[PreferencesKeys.SELECTED_FONT] ?: LipikaPreferences.FONT_TW_CENT_MT,
                selectedBengaliFont = prefs[PreferencesKeys.SELECTED_BENGALI_FONT] ?: LipikaPreferences.FONT_KALPURUSH,
                accentColor = prefs[PreferencesKeys.ACCENT_COLOR] ?: LipikaPreferences.ACCENT_CYAN,
                accentMode = prefs[PreferencesKeys.ACCENT_MODE] ?: LipikaPreferences.ACCENT_MODE_SINGLE,
                multipleAccentsRaw = prefs[PreferencesKeys.MULTIPLE_ACCENTS] ?: "",
                customFontPath = prefs[PreferencesKeys.CUSTOM_FONT_PATH] ?: "",
                keyboardHeightScale = prefs[PreferencesKeys.KEYBOARD_HEIGHT_SCALE] ?: 1.0f,
                keyboardHeightScaleLandscape = prefs[PreferencesKeys.KEYBOARD_HEIGHT_SCALE_LANDSCAPE] ?: 1.0f,
                chinPaddingPortraitDp = prefs[PreferencesKeys.CHIN_PADDING_PORTRAIT] ?: 8.0f,
                chinPaddingLandscapeDp = prefs[PreferencesKeys.CHIN_PADDING_LANDSCAPE] ?: 4.0f,
                horizontalMarginPortraitDp = prefs[PreferencesKeys.HORIZONTAL_MARGIN_PORTRAIT] ?: 0.0f,
                horizontalMarginLandscapeDp = prefs[PreferencesKeys.HORIZONTAL_MARGIN_LANDSCAPE] ?: 0.0f,
                showNumberRow = prefs[PreferencesKeys.SHOW_NUMBER_ROW] ?: false,
                showKeyBorders = prefs[PreferencesKeys.SHOW_KEY_BORDERS] ?: true,
                showKeyShadows = prefs[PreferencesKeys.SHOW_KEY_SHADOWS] ?: true,
                showKeyPopups = prefs[PreferencesKeys.SHOW_KEY_POPUPS] ?: true,
                showSecondaryKeys = prefs[PreferencesKeys.SHOW_SECONDARY_KEYS] ?: true,

                // Appearance Screen Settings
                displayMode = prefs[PreferencesKeys.DISPLAY_MODE] ?: LipikaPreferences.DISPLAY_MODE_SYSTEM,
                appTheme = prefs[PreferencesKeys.APP_THEME] ?: LipikaPreferences.APP_THEME_SYSTEM,
                keyboardThemeMode = prefs[PreferencesKeys.KEYBOARD_THEME_MODE] ?: LipikaPreferences.KEYBOARD_THEME_DARK,
                darkStyle = prefs[PreferencesKeys.DARK_STYLE] ?: LipikaPreferences.DARK_STYLE_DARK_GRAY,
                themeSchedule = prefs[PreferencesKeys.THEME_SCHEDULE] ?: LipikaPreferences.SCHEDULE_ALWAYS_DARK,
                customScheduleStartHour = prefs[PreferencesKeys.CUSTOM_SCHEDULE_START_HOUR] ?: 18,
                customScheduleEndHour = prefs[PreferencesKeys.CUSTOM_SCHEDULE_END_HOUR] ?: 6,

                vibrateOnKeypress = prefs[PreferencesKeys.VIBRATE_ON_KEYPRESS] ?: false,
                vibrationType = prefs[PreferencesKeys.VIBRATION_TYPE] ?: LipikaPreferences.VIBRATION_TYPE_HAPTIC,
                vibrationStrength = prefs[PreferencesKeys.VIBRATION_STRENGTH] ?: 50,
                vibrationDurationMs = prefs[PreferencesKeys.VIBRATION_DURATION_MS] ?: 25,
                soundOnKeypress = prefs[PreferencesKeys.SOUND_ON_KEYPRESS] ?: false,
                soundVolume = prefs[PreferencesKeys.SOUND_VOLUME] ?: 80,
                soundProfile = prefs[PreferencesKeys.SOUND_PROFILE] ?: LipikaPreferences.SOUND_PROFILE_DEFAULT,
                soundDistinctKeys = prefs[PreferencesKeys.SOUND_DISTINCT_KEYS] ?: true,
                vibrateOnLongPress = prefs[PreferencesKeys.VIBRATE_ON_LONG_PRESS] ?: true,
                vibrateOnSpaceSwipe = prefs[PreferencesKeys.VIBRATE_ON_SPACE_SWIPE] ?: true,
                longPressDelayMs = prefs[PreferencesKeys.LONG_PRESS_DELAY_MS] ?: 300,
                cursorTrackpadEnabled = prefs[PreferencesKeys.CURSOR_TRACKPAD_ENABLED] ?: true,
                oneHandedMode = prefs[PreferencesKeys.ONE_HANDED_MODE] ?: LipikaPreferences.ONE_HANDED_OFF,
                longPressDeleteEnabled = prefs[PreferencesKeys.LONG_PRESS_DELETE_ENABLED] ?: true,
                selectiveDeleteMode = prefs[PreferencesKeys.SELECTIVE_DELETE_MODE] ?: LipikaPreferences.DELETE_MODE_WORD,

                appLanguage = prefs[PreferencesKeys.APP_LANGUAGE] ?: LipikaPreferences.APP_LANG_EN,
                primaryLayoutId = prefs[PreferencesKeys.PRIMARY_LAYOUT_ID] ?: LipikaPreferences.LAYOUT_EN_QWERTY,
                enabledLayoutIds = run {
                    val rawSet = prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] ?: linkedSetOf(
                        LipikaPreferences.LAYOUT_EN_QWERTY,
                        LipikaPreferences.LAYOUT_BN_PROBHAT,
                        LipikaPreferences.LAYOUT_BN_AVRO
                    )
                    val orderRaw = prefs[PreferencesKeys.ENABLED_LAYOUT_ORDER]
                    if (!orderRaw.isNullOrBlank()) {
                        val ordered = orderRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() && it in rawSet }
                        val remaining = rawSet.filter { it !in ordered }
                        LinkedHashSet(ordered + remaining)
                    } else {
                        rawSet
                    }
                },

                suggestionsEnabled = prefs[PreferencesKeys.SUGGESTIONS_ENABLED] ?: true,
                nextWordPrediction = prefs[PreferencesKeys.NEXT_WORD_PREDICTION] ?: true,
                emojiPredictionEnabled = prefs[PreferencesKeys.EMOJI_PREDICTION_ENABLED] ?: true,
                filterOffensiveWords = prefs[PreferencesKeys.FILTER_OFFENSIVE_WORDS] ?: true,
                autoCorrection = prefs[PreferencesKeys.AUTO_CORRECTION] ?: false,
                phoneticAutoCorrect = prefs[PreferencesKeys.PHONETIC_AUTO_CORRECT] ?: false,
                smartSpellChecker = prefs[PreferencesKeys.SMART_SPELL_CHECKER] ?: true,
                customWordLearning = prefs[PreferencesKeys.CUSTOM_WORD_LEARNING] ?: true,
                bengaliSmartPhonetic = prefs[PreferencesKeys.BENGALI_SMART_PHONETIC] ?: true,
                bengaliShowRawInputCandidate = prefs[PreferencesKeys.BENGALI_SHOW_RAW_INPUT_CANDIDATE] ?: false,
                learnedWordsEnabled = prefs[PreferencesKeys.LEARNED_WORDS_ENABLED] ?: true,
                gestureTypingEnabled = prefs[PreferencesKeys.GESTURE_TYPING_ENABLED] ?: true,
                showGestureTrail = prefs[PreferencesKeys.SHOW_GESTURE_TRAIL] ?: true,
                autoCapitalization = prefs[PreferencesKeys.AUTO_CAPITALIZATION] ?: true,
                doubleSpacePeriod = prefs[PreferencesKeys.DOUBLE_SPACE_PERIOD] ?: true,
                autoSpaceAfterSuggestion = prefs[PreferencesKeys.AUTO_SPACE_AFTER_SUGGESTION] ?: true,
                autoSpaceAfterPunctuation = prefs[PreferencesKeys.AUTO_SPACE_AFTER_PUNCTUATION] ?: true,
                removeSpaceBeforePunctuation = prefs[PreferencesKeys.REMOVE_SPACE_BEFORE_PUNCTUATION] ?: true,
                traditionalMode = prefs[PreferencesKeys.TRADITIONAL_MODE] ?: false,
                suggestionBarMode = prefs[PreferencesKeys.SUGGESTION_BAR_MODE] ?: LipikaPreferences.SUGGESTION_BAR_MODE_NORMAL,

                pinnedTools = run {
                    val defaultPinned = listOf("EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE")
                    val allActionNames = listOf(
                        "EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE", "RESIZER",
                        "NUMBER_ROW_TOGGLE", "SECONDARY_KEYS_TOGGLE", "KEY_POPUP_TOGGLE", "ONE_HANDED", "SETTINGS", "TRADITIONAL_MODE",
                        "UNDO", "REDO"
                    )
                    prefs[PreferencesKeys.PINNED_TOOLS]?.let { raw ->
                        raw.split(",").filter { it in allActionNames }.distinct()
                    } ?: defaultPinned
                },
                drawerTools = run {
                    val defaultPinned = listOf("EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE")
                    val defaultDrawer = listOf("RESIZER", "NUMBER_ROW_TOGGLE", "SECONDARY_KEYS_TOGGLE", "KEY_POPUP_TOGGLE", "ONE_HANDED", "SETTINGS", "TRADITIONAL_MODE", "UNDO", "REDO")
                    val allActionNames = listOf(
                        "EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE", "RESIZER",
                        "NUMBER_ROW_TOGGLE", "SECONDARY_KEYS_TOGGLE", "KEY_POPUP_TOGGLE", "ONE_HANDED", "SETTINGS", "TRADITIONAL_MODE",
                        "UNDO", "REDO"
                    )
                    val pinnedList = prefs[PreferencesKeys.PINNED_TOOLS]?.let { raw ->
                        raw.split(",").filter { it in allActionNames }.distinct()
                    } ?: defaultPinned
                    val pinnedSet = pinnedList.toSet()
                    val rawDrawer = prefs[PreferencesKeys.DRAWER_TOOLS]?.split(",")?.filter { it in allActionNames } ?: defaultDrawer
                    (rawDrawer.filter { it !in pinnedSet } +
                        allActionNames.filter { it !in pinnedSet }).distinct()
                },
                voiceTypingEnabled = prefs[PreferencesKeys.VOICE_TYPING_ENABLED] ?: true,
                voiceTypingProvider = prefs[PreferencesKeys.VOICE_TYPING_PROVIDER] ?: LipikaPreferences.VOICE_PROVIDER_GOOGLE,
                extraRowOverrides = run {
                    val overrides = mutableMapOf<String, Boolean>()
                    for ((key, value) in prefs.asMap()) {
                        if (key.name.startsWith("pref_extra_row_") && value is Boolean) {
                            overrides[key.name.removePrefix("pref_extra_row_")] = value
                        }
                    }
                    overrides
                },
                clipboardExpiryHours = prefs[PreferencesKeys.CLIPBOARD_EXPIRY_HOURS] ?: 5,
                statsBanglaLetters = prefs[PreferencesKeys.STATS_BANGLA_LETTERS] ?: 0L,
                statsEnglishLetters = prefs[PreferencesKeys.STATS_ENGLISH_LETTERS] ?: 0L,
                statsAvroWords = prefs[PreferencesKeys.STATS_AVRO_WORDS] ?: 0L,
                statsWordsTyped = prefs[PreferencesKeys.STATS_WORDS_TYPED] ?: 0L,
                statsNumbersTyped = prefs[PreferencesKeys.STATS_NUMBERS_TYPED] ?: 0L,
                statsKeystrokes = prefs[PreferencesKeys.STATS_KEYSTROKES] ?: 0L,
                statsAudioClicks = prefs[PreferencesKeys.STATS_AUDIO_CLICKS] ?: 0L,
                statsBackspaces = prefs[PreferencesKeys.STATS_BACKSPACES] ?: 0L,
                statsEmojis = prefs[PreferencesKeys.STATS_EMOJIS] ?: 0L,
                statsSymbols = prefs[PreferencesKeys.STATS_SYMBOLS] ?: 0L,

                setupCompleted = (prefs[PreferencesKeys.SETUP_COMPLETED] ?: false).also { isCompleted ->
                    if (isCompleted && !fastCache.getBoolean("setup_completed", false)) {
                        fastCache.edit().putBoolean("setup_completed", true).apply()
                    }
                } || fastCache.getBoolean("setup_completed", false)
            )
        }

    // Appearance & Theming mutators
    suspend fun updateAccentColor(accent: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.ACCENT_COLOR] = accent }
    }

    suspend fun updateAccentMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.ACCENT_MODE] = mode }
    }

    suspend fun updateMultipleAccents(map: Map<String, String>) {
        val raw = map.entries.joinToString(",") { "${it.key}:${it.value}" }
        context.lipikaDataStore.edit { it[PreferencesKeys.MULTIPLE_ACCENTS] = raw }
    }

    suspend fun updateComponentAccent(component: String, hexColor: String) {
        context.lipikaDataStore.edit { prefs ->
            val currentRaw = prefs[PreferencesKeys.MULTIPLE_ACCENTS] ?: ""
            val currentMap = if (currentRaw.isBlank()) mutableMapOf() else currentRaw.split(",")
                .mapNotNull { item ->
                    val parts = item.split(":")
                    if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                        parts[0].trim() to parts[1].trim()
                    } else null
                }.toMap().toMutableMap()
            currentMap[component] = hexColor
            prefs[PreferencesKeys.MULTIPLE_ACCENTS] = currentMap.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    suspend fun updateTheme(theme: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.THEME] = theme }
    }

    suspend fun updateDisplayMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.DISPLAY_MODE] = mode }
    }

    suspend fun updateAppTheme(appTheme: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.APP_THEME] = appTheme }
    }

    suspend fun updateKeyboardThemeMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.KEYBOARD_THEME_MODE] = mode }
    }

    suspend fun updateDarkStyle(style: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.DARK_STYLE] = style }
    }

    suspend fun updateThemeSchedule(schedule: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.THEME_SCHEDULE] = schedule }
    }

    suspend fun updateCustomScheduleHours(startHour: Int, endHour: Int) {
        context.lipikaDataStore.edit {
            it[PreferencesKeys.CUSTOM_SCHEDULE_START_HOUR] = startHour
            it[PreferencesKeys.CUSTOM_SCHEDULE_END_HOUR] = endHour
        }
    }

    suspend fun updateSelectedFont(font: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SELECTED_FONT] = font }
    }

    suspend fun updateSelectedBengaliFont(font: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SELECTED_BENGALI_FONT] = font }
    }

    suspend fun updateCustomFontPath(path: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.CUSTOM_FONT_PATH] = path }
    }

    suspend fun updateKeyboardHeightScale(scale: Float) {
        context.lipikaDataStore.edit { it[PreferencesKeys.KEYBOARD_HEIGHT_SCALE] = scale }
    }

    suspend fun updateKeyboardHeightScaleLandscape(scale: Float) {
        context.lipikaDataStore.edit { it[PreferencesKeys.KEYBOARD_HEIGHT_SCALE_LANDSCAPE] = scale }
    }

    suspend fun updateKeyboardResizerValues(
        isLandscape: Boolean,
        heightScale: Float,
        chinPaddingDp: Float,
        horizontalMarginDp: Float
    ) {
        context.lipikaDataStore.edit { prefs ->
            if (isLandscape) {
                prefs[PreferencesKeys.KEYBOARD_HEIGHT_SCALE_LANDSCAPE] = heightScale
                prefs[PreferencesKeys.CHIN_PADDING_LANDSCAPE] = chinPaddingDp
                prefs[PreferencesKeys.HORIZONTAL_MARGIN_LANDSCAPE] = horizontalMarginDp
            } else {
                prefs[PreferencesKeys.KEYBOARD_HEIGHT_SCALE] = heightScale
                prefs[PreferencesKeys.CHIN_PADDING_PORTRAIT] = chinPaddingDp
                prefs[PreferencesKeys.HORIZONTAL_MARGIN_PORTRAIT] = horizontalMarginDp
            }
        }
    }

    suspend fun updateShowNumberRow(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_NUMBER_ROW] = enabled }
    }

    suspend fun updateShowKeyBorders(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_KEY_BORDERS] = enabled }
    }

    suspend fun updateShowKeyShadows(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_KEY_SHADOWS] = enabled }
    }

    suspend fun updateShowKeyPopups(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_KEY_POPUPS] = enabled }
    }

    suspend fun updateShowSecondaryKeys(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_SECONDARY_KEYS] = enabled }
    }

    // Preferences & Haptics mutators
    suspend fun updateVibrateOnKeypress(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATE_ON_KEYPRESS] = enabled }
    }

    suspend fun updateVibrationType(type: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATION_TYPE] = type }
    }

    suspend fun updateVibrationStrength(strength: Int) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATION_STRENGTH] = strength.coerceIn(0, 100) }
    }

    suspend fun updateVibrationDuration(durationMs: Int) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATION_DURATION_MS] = durationMs.coerceIn(5, 100) }
    }

    suspend fun updateVibrationDurationMs(durationMs: Int) {
        updateVibrationDuration(durationMs)
    }

    suspend fun updateSoundOnKeypress(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SOUND_ON_KEYPRESS] = enabled }
    }

    suspend fun updateSoundVolume(volume: Int) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SOUND_VOLUME] = volume.coerceIn(0, 100) }
    }

    suspend fun updateLongPressDelayMs(delayMs: Int) {
        context.lipikaDataStore.edit { it[PreferencesKeys.LONG_PRESS_DELAY_MS] = delayMs.coerceIn(150, 600) }
    }

    suspend fun updateCursorTrackpadEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.CURSOR_TRACKPAD_ENABLED] = enabled }
    }

    suspend fun updateOneHandedMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.ONE_HANDED_MODE] = mode }
    }

    suspend fun updateLongPressDeleteEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.LONG_PRESS_DELETE_ENABLED] = enabled }
    }

    suspend fun updateSelectiveDeleteMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SELECTIVE_DELETE_MODE] = mode }
    }

    suspend fun updateAppLanguage(lang: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.APP_LANGUAGE] = lang }
    }

    // Layout mutators
    suspend fun updatePrimaryLayoutId(layoutId: String) {
        context.lipikaDataStore.edit { prefs ->
            prefs[PreferencesKeys.PRIMARY_LAYOUT_ID] = layoutId
            val currentEnabled = prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] ?: linkedSetOf(
                LipikaPreferences.LAYOUT_EN_QWERTY,
                LipikaPreferences.LAYOUT_BN_BORNO,
                LipikaPreferences.LAYOUT_BN_AVRO,
                LipikaPreferences.LAYOUT_BN_EASY,
                LipikaPreferences.LAYOUT_BN_NATIONAL
            )
            if (!currentEnabled.contains(layoutId)) {
                prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] = currentEnabled + layoutId
            }
        }
    }

    suspend fun toggleLayoutEnabled(layoutId: String, enabled: Boolean) {
        context.lipikaDataStore.edit { prefs ->
            val currentEnabled = (prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] ?: linkedSetOf(
                LipikaPreferences.LAYOUT_EN_QWERTY,
                LipikaPreferences.LAYOUT_BN_BORNO,
                LipikaPreferences.LAYOUT_BN_AVRO,
                LipikaPreferences.LAYOUT_BN_EASY,
                LipikaPreferences.LAYOUT_BN_NATIONAL
            )).toMutableSet()

            if (enabled) {
                currentEnabled.add(layoutId)
            } else {
                // Ensure at least one layout is enabled
                if (currentEnabled.size > 1) {
                    currentEnabled.remove(layoutId)
                    // If primary was removed, point primary to the first available layout
                    val primary = prefs[PreferencesKeys.PRIMARY_LAYOUT_ID]
                    if (primary == layoutId) {
                        prefs[PreferencesKeys.PRIMARY_LAYOUT_ID] = currentEnabled.first()
                    }
                }
            }
            prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] = currentEnabled
            val existingOrder = prefs[PreferencesKeys.ENABLED_LAYOUT_ORDER]
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() && it in currentEnabled }
                ?.toMutableList()
                ?: mutableListOf()
            for (id in currentEnabled) {
                if (id !in existingOrder) existingOrder.add(id)
            }
            prefs[PreferencesKeys.ENABLED_LAYOUT_ORDER] = existingOrder.joinToString(",")
        }
    }

    suspend fun updateEnabledLayoutsOrder(orderedIds: List<String>) {
        if (orderedIds.isEmpty()) return
        context.lipikaDataStore.edit { prefs ->
            val distinctIds = orderedIds.distinct()
            prefs[PreferencesKeys.ENABLED_LAYOUT_IDS] = distinctIds.toSet()
            prefs[PreferencesKeys.ENABLED_LAYOUT_ORDER] = distinctIds.joinToString(",")
            prefs[PreferencesKeys.PRIMARY_LAYOUT_ID] = distinctIds.first()
        }
    }

    // Suggestions & Corrections mutators
    suspend fun updateSuggestionsEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SUGGESTIONS_ENABLED] = enabled }
    }

    suspend fun updateNextWordPrediction(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.NEXT_WORD_PREDICTION] = enabled }
    }

    suspend fun updateEmojiPredictionEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.EMOJI_PREDICTION_ENABLED] = enabled }
    }

    suspend fun updateFilterOffensiveWords(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.FILTER_OFFENSIVE_WORDS] = enabled }
    }

    suspend fun updateAutoCorrection(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.AUTO_CORRECTION] = enabled }
    }

    suspend fun updateSmartSpellChecker(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SMART_SPELL_CHECKER] = enabled }
    }

    suspend fun updateCustomWordLearning(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.CUSTOM_WORD_LEARNING] = enabled }
    }

    suspend fun updatePhoneticAutoCorrect(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.PHONETIC_AUTO_CORRECT] = enabled }
    }

    suspend fun updateBengaliSmartPhonetic(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.BENGALI_SMART_PHONETIC] = enabled }
    }

    suspend fun updateBengaliShowRawInputCandidate(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.BENGALI_SHOW_RAW_INPUT_CANDIDATE] = enabled }
    }

    suspend fun updateLearnedWordsEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.LEARNED_WORDS_ENABLED] = enabled }
    }

    suspend fun updateGestureTypingEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.GESTURE_TYPING_ENABLED] = enabled }
    }

    suspend fun updateShowGestureTrail(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SHOW_GESTURE_TRAIL] = enabled }
    }

    suspend fun updateAutoCapitalization(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.AUTO_CAPITALIZATION] = enabled }
    }

    suspend fun updateDoubleSpacePeriod(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.DOUBLE_SPACE_PERIOD] = enabled }
    }

    suspend fun updateAutoSpaceAfterSuggestion(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.AUTO_SPACE_AFTER_SUGGESTION] = enabled }
    }

    suspend fun updateAutoSpaceAfterPunctuation(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.AUTO_SPACE_AFTER_PUNCTUATION] = enabled }
    }

    suspend fun updateRemoveSpaceBeforePunctuation(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.REMOVE_SPACE_BEFORE_PUNCTUATION] = enabled }
    }

    suspend fun updateTraditionalMode(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.TRADITIONAL_MODE] = enabled }
    }

    suspend fun updateSuggestionBarMode(mode: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SUGGESTION_BAR_MODE] = mode }
    }

    suspend fun updateToolbarTools(pinned: List<String>, drawer: List<String>) {
        val defaultPinned = listOf("EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE")
        val allActionNames = listOf(
            "EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE", "RESIZER",
            "NUMBER_ROW_TOGGLE", "SECONDARY_KEYS_TOGGLE", "KEY_POPUP_TOGGLE", "ONE_HANDED", "SETTINGS", "TRADITIONAL_MODE",
            "UNDO", "REDO"
        )
        val sanitizedPinned = pinned.filter { it in allActionNames }.distinct()
        val pinnedSet = sanitizedPinned.toSet()
        val sanitizedDrawer = (drawer.filter { it in allActionNames && it !in pinnedSet } +
            allActionNames.filter { it !in pinnedSet }).distinct()

        context.lipikaDataStore.edit {
            it[PreferencesKeys.PINNED_TOOLS] = sanitizedPinned.joinToString(",")
            it[PreferencesKeys.DRAWER_TOOLS] = sanitizedDrawer.joinToString(",")
        }
    }

    suspend fun updateVoiceTypingEnabled(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VOICE_TYPING_ENABLED] = enabled }
    }

    suspend fun updateVoiceTypingProvider(provider: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VOICE_TYPING_PROVIDER] = provider }
    }

    suspend fun updateSetupCompleted(completed: Boolean) {
        fastCache.edit().putBoolean("setup_completed", completed).apply()
        context.lipikaDataStore.edit { it[PreferencesKeys.SETUP_COMPLETED] = completed }
    }

    suspend fun setLayoutExtraRowEnabled(layoutId: String, rowId: String, enabled: Boolean) {
        val key = booleanPreferencesKey("pref_extra_row_${layoutId}_${rowId}")
        context.lipikaDataStore.edit { it[key] = enabled }
    }

    suspend fun updateClipboardExpiryHours(hours: Int) {
        fastCache.edit().putInt("pref_clipboard_expiry_hours", hours).apply()
        context.lipikaDataStore.edit { it[PreferencesKeys.CLIPBOARD_EXPIRY_HOURS] = hours }
    }

    fun getClipboardExpiryHoursSync(): Int {
        return fastCache.getInt("pref_clipboard_expiry_hours", 5)
    }

    suspend fun updateSoundProfile(profile: String) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SOUND_PROFILE] = profile }
    }

    suspend fun updateSoundDistinctKeys(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.SOUND_DISTINCT_KEYS] = enabled }
    }

    suspend fun updateVibrateOnLongPress(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATE_ON_LONG_PRESS] = enabled }
    }

    suspend fun updateVibrateOnSpaceSwipe(enabled: Boolean) {
        context.lipikaDataStore.edit { it[PreferencesKeys.VIBRATE_ON_SPACE_SWIPE] = enabled }
    }

    suspend fun recordStatsBatch(
        bangla: Long = 0L,
        english: Long = 0L,
        avro: Long = 0L,
        words: Long = 0L,
        numbers: Long = 0L,
        keystrokes: Long = 0L,
        audio: Long = 0L,
        backspaces: Long = 0L,
        emojis: Long = 0L,
        symbols: Long = 0L
    ) {
        context.lipikaDataStore.edit { prefs ->
            if (bangla > 0L) {
                val current = prefs[PreferencesKeys.STATS_BANGLA_LETTERS] ?: 0L
                prefs[PreferencesKeys.STATS_BANGLA_LETTERS] = current + bangla
            }
            if (english > 0L) {
                val current = prefs[PreferencesKeys.STATS_ENGLISH_LETTERS] ?: 0L
                prefs[PreferencesKeys.STATS_ENGLISH_LETTERS] = current + english
            }
            if (avro > 0L) {
                val current = prefs[PreferencesKeys.STATS_AVRO_WORDS] ?: 0L
                prefs[PreferencesKeys.STATS_AVRO_WORDS] = current + avro
            }
            if (words > 0L) {
                val current = prefs[PreferencesKeys.STATS_WORDS_TYPED] ?: 0L
                prefs[PreferencesKeys.STATS_WORDS_TYPED] = current + words
            }
            if (numbers > 0L) {
                val current = prefs[PreferencesKeys.STATS_NUMBERS_TYPED] ?: 0L
                prefs[PreferencesKeys.STATS_NUMBERS_TYPED] = current + numbers
            }
            if (keystrokes > 0L) {
                val current = prefs[PreferencesKeys.STATS_KEYSTROKES] ?: 0L
                prefs[PreferencesKeys.STATS_KEYSTROKES] = current + keystrokes
            }
            if (audio > 0L) {
                val current = prefs[PreferencesKeys.STATS_AUDIO_CLICKS] ?: 0L
                prefs[PreferencesKeys.STATS_AUDIO_CLICKS] = current + audio
            }
            if (backspaces > 0L) {
                val current = prefs[PreferencesKeys.STATS_BACKSPACES] ?: 0L
                prefs[PreferencesKeys.STATS_BACKSPACES] = current + backspaces
            }
            if (emojis > 0L) {
                val current = prefs[PreferencesKeys.STATS_EMOJIS] ?: 0L
                prefs[PreferencesKeys.STATS_EMOJIS] = current + emojis
            }
            if (symbols > 0L) {
                val current = prefs[PreferencesKeys.STATS_SYMBOLS] ?: 0L
                prefs[PreferencesKeys.STATS_SYMBOLS] = current + symbols
            }
        }
    }

    suspend fun resetStatistics() {
        context.lipikaDataStore.edit { prefs ->
            prefs[PreferencesKeys.STATS_BANGLA_LETTERS] = 0L
            prefs[PreferencesKeys.STATS_ENGLISH_LETTERS] = 0L
            prefs[PreferencesKeys.STATS_AVRO_WORDS] = 0L
            prefs[PreferencesKeys.STATS_WORDS_TYPED] = 0L
            prefs[PreferencesKeys.STATS_NUMBERS_TYPED] = 0L
            prefs[PreferencesKeys.STATS_KEYSTROKES] = 0L
            prefs[PreferencesKeys.STATS_AUDIO_CLICKS] = 0L
            prefs[PreferencesKeys.STATS_BACKSPACES] = 0L
            prefs[PreferencesKeys.STATS_EMOJIS] = 0L
            prefs[PreferencesKeys.STATS_SYMBOLS] = 0L
        }
    }

    // Reset
    suspend fun resetToDefaults() {
        fastCache.edit().clear().apply()
        context.lipikaDataStore.edit { it.clear() }
    }

    companion object {
        @Volatile
        private var INSTANCE: LipikaPreferencesRepository? = null

        fun getInstance(context: Context): LipikaPreferencesRepository {
            val safeContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val app = context.applicationContext ?: context
                if (app.isDeviceProtectedStorage) app else app.createDeviceProtectedStorageContext()
            } else {
                context.applicationContext ?: context
            }
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LipikaPreferencesRepository(safeContext).also { INSTANCE = it }
            }
        }
    }
}
