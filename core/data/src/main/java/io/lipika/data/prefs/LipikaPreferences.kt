package io.lipika.data.prefs

/**
 * Immutable snapshot of all user-configurable settings and preferences for Lipika.
 */
data class LipikaPreferences(
    // Appearance & Theming
    val theme: String = THEME_LIPIKA_GRADIENT,
    val selectedFont: String = FONT_TW_CENT_MT,
    val selectedBengaliFont: String = FONT_KALPURUSH,
    val customFontPath: String = "",
    val accentColor: String = ACCENT_CYAN,
    val accentMode: String = ACCENT_MODE_SINGLE,
    val multipleAccentsRaw: String = "",
    val keyboardHeightScale: Float = 1.0f,
    val keyboardHeightScaleLandscape: Float = 1.0f,
    val chinPaddingPortraitDp: Float = 8.0f,
    val chinPaddingLandscapeDp: Float = 4.0f,
    val horizontalMarginPortraitDp: Float = 0.0f,
    val horizontalMarginLandscapeDp: Float = 0.0f,
    val showNumberRow: Boolean = false,
    val showKeyBorders: Boolean = true,
    val showKeyShadows: Boolean = true,
    val showKeyPopups: Boolean = true,
    val showSecondaryKeys: Boolean = true,

    // Appearance Screen Settings
    val displayMode: String = DISPLAY_MODE_SYSTEM,
    val appTheme: String = APP_THEME_SYSTEM,
    val keyboardThemeMode: String = KEYBOARD_THEME_DARK,
    val darkStyle: String = DARK_STYLE_DARK_GRAY,
    val themeSchedule: String = SCHEDULE_ALWAYS_DARK,
    val customScheduleStartHour: Int = 18,
    val customScheduleEndHour: Int = 6,

    // Preferences & Haptics
    val vibrateOnKeypress: Boolean = false,
    val vibrationType: String = VIBRATION_TYPE_HAPTIC,
    val vibrationStrength: Int = 50, // 0 - 100%
    val vibrationDurationMs: Int = 25, // 5 - 100 ms
    val soundOnKeypress: Boolean = false,
    val soundVolume: Int = 80, // 0 - 100%
    val soundProfile: String = SOUND_PROFILE_DEFAULT, // "default", "mechanical", "typewriter", "bubble"
    val soundDistinctKeys: Boolean = true,
    val vibrateOnLongPress: Boolean = true,
    val vibrateOnSpaceSwipe: Boolean = true,
    val longPressDelayMs: Int = 220, // 100 - 600 ms
    val cursorTrackpadEnabled: Boolean = true,
    val oneHandedMode: String = ONE_HANDED_OFF,
    val longPressDeleteEnabled: Boolean = true,
    val selectiveDeleteMode: String = DELETE_MODE_WORD,

    // Keyboard Layouts & Language
    val appLanguage: String = APP_LANG_EN,
    val primaryLayoutId: String = LAYOUT_EN_QWERTY,
    val enabledLayoutIds: Set<String> = linkedSetOf(
        LAYOUT_EN_QWERTY,
        LAYOUT_BN_PROBHAT,
        LAYOUT_BN_AVRO
    ),

    // Smart Typing: Corrections & Suggestions
    val suggestionsEnabled: Boolean = true,
    val nextWordPrediction: Boolean = true,
    val emojiPredictionEnabled: Boolean = true,
    val learnedWordsEnabled: Boolean = true,
    val filterOffensiveWords: Boolean = true,
    val autoCorrection: Boolean = false,
    val phoneticAutoCorrect: Boolean = false,
    val smartSpellChecker: Boolean = true,
    val customWordLearning: Boolean = true,
    val bengaliSmartPhonetic: Boolean = true,
    val bengaliShowRawInputCandidate: Boolean = false,
    val gestureTypingEnabled: Boolean = true,
    val showGestureTrail: Boolean = true,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val autoSpaceAfterSuggestion: Boolean = true,
    val autoSpaceAfterPunctuation: Boolean = true,
    val removeSpaceBeforePunctuation: Boolean = true,
    val traditionalMode: Boolean = false,
    val suggestionBarMode: String = SUGGESTION_BAR_MODE_NORMAL,

    // Toolbar rearrangement persistence
    val pinnedTools: List<String> = listOf("EMOJI", "TEXT_EDIT", "NUMBER_PAD", "CLIPBOARD", "THEME_TOGGLE"),
    val drawerTools: List<String> = listOf("RESIZER", "NUMBER_ROW_TOGGLE", "SECONDARY_KEYS_TOGGLE", "KEY_POPUP_TOGGLE", "ONE_HANDED", "SETTINGS", "TRADITIONAL_MODE", "UNDO", "REDO"),

    // Voice Typing
    val voiceTypingEnabled: Boolean = true,
    val voiceTypingProvider: String = VOICE_PROVIDER_GOOGLE,

    // Layout-specific dynamic extra row overrides ("${layoutId}_${rowId}" -> Boolean)
    val extraRowOverrides: Map<String, Boolean> = emptyMap(),

    // Clipboard & Retention
    val clipboardExpiryHours: Int = 5, // -1 = Never, 1, 2, 5, 6, 12, 24, 360, 720

    // Typing Statistics
    val statsBanglaLetters: Long = 0L,
    val statsEnglishLetters: Long = 0L,
    val statsAvroWords: Long = 0L,
    val statsWordsTyped: Long = 0L,
    val statsNumbersTyped: Long = 0L,
    val statsKeystrokes: Long = 0L,
    val statsAudioClicks: Long = 0L,
    val statsBackspaces: Long = 0L,
    val statsEmojis: Long = 0L,
    val statsSymbols: Long = 0L,

    // Onboarding / Setup status
    val setupCompleted: Boolean = false
) {
    fun isExtraRowEnabled(layoutId: String, rowId: String, default: Boolean = true): Boolean {
        return extraRowOverrides["${layoutId}_${rowId}"] ?: default
    }

    fun resolveEffectiveIsDark(systemInDark: Boolean): Boolean {
        return when (themeSchedule) {
            SCHEDULE_SYSTEM_DEFAULT -> systemInDark
            SCHEDULE_ALWAYS_LIGHT -> false
            SCHEDULE_ALWAYS_DARK -> true
            SCHEDULE_SUNSET_TO_SUNRISE -> {
                val cal = java.util.Calendar.getInstance()
                val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                hour >= 18 || hour < 6
            }
            SCHEDULE_CUSTOM -> {
                val cal = java.util.Calendar.getInstance()
                val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                if (customScheduleStartHour <= customScheduleEndHour) {
                    hour in customScheduleStartHour until customScheduleEndHour
                } else {
                    hour >= customScheduleStartHour || hour < customScheduleEndHour
                }
            }
            else -> {
                when {
                    appTheme == APP_THEME_LIGHT -> false
                    appTheme == APP_THEME_DARK -> true
                    displayMode == DISPLAY_MODE_LIGHT -> false
                    displayMode == DISPLAY_MODE_DARK -> true
                    else -> systemInDark
                }
            }
        }
    }

    fun effectiveKeyboardTheme(): String {
        return when (keyboardThemeMode) {
            KEYBOARD_THEME_LIGHT -> THEME_DEFAULT_LIGHT
            KEYBOARD_THEME_DARK -> when (darkStyle) {
                DARK_STYLE_PURE_BLACK -> THEME_PURE_BLACK
                DARK_STYLE_AMOLED -> THEME_AMOLED_BLACK
                else -> THEME_DEFAULT_DARK
            }
            KEYBOARD_THEME_CUSTOM -> theme
            else -> theme
        }
    }

    fun getMultipleAccentsMap(): Map<String, String> {
        if (multipleAccentsRaw.isBlank()) return emptyMap()
        return multipleAccentsRaw.split(",")
            .mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                    parts[0].trim() to parts[1].trim()
                } else null
            }
            .toMap()
    }

    companion object {
        const val VOICE_PROVIDER_GOOGLE = "google"

        const val DISPLAY_MODE_LIGHT = "light"
        const val DISPLAY_MODE_DARK = "dark"
        const val DISPLAY_MODE_SYSTEM = "system"

        const val APP_THEME_LIGHT = "light"
        const val APP_THEME_DARK = "dark"
        const val APP_THEME_SYSTEM = "system"
        const val APP_THEME_DYNAMIC = "dynamic"

        const val KEYBOARD_THEME_LIGHT = "light"
        const val KEYBOARD_THEME_DARK = "dark"
        const val KEYBOARD_THEME_CUSTOM = "custom"

        const val DARK_STYLE_PURE_BLACK = "pure_black"
        const val DARK_STYLE_AMOLED = "amoled"
        const val DARK_STYLE_DARK_GRAY = "dark_gray"

        const val SCHEDULE_SYSTEM_DEFAULT = "system_default"
        const val SCHEDULE_ALWAYS_LIGHT = "always_light"
        const val SCHEDULE_ALWAYS_DARK = "always_dark"
        const val SCHEDULE_SUNSET_TO_SUNRISE = "sunset_to_sunrise"
        const val SCHEDULE_CUSTOM = "custom"

        const val THEME_PURE_BLACK = "pure_black"

        const val FONT_DEFAULT = "default"
        const val FONT_TW_CENT_MT = "tw_cent_mt"
        const val FONT_KALPURUSH = "kalpurush"
        const val FONT_SEGOE_UI = "segoe_ui"
        const val FONT_GEORGIA = "georgia"
        const val FONT_CONSOLAS = "consolas"
        const val FONT_CANDARA = "candara"
        const val FONT_COMIC_SANS = "comic_sans"
        const val FONT_TREBUCHET = "trebuchet"
        const val FONT_VERDANA = "verdana"
        const val FONT_CALIBRI = "calibri"
        const val FONT_CORBEL = "corbel"

        // 15 Bengali Fonts
        const val FONT_SOLAIMAN_LIPI = "solaiman_lipi"
        const val FONT_SIYAM_RUPALI = "siyam_rupali"
        const val FONT_NOTO_BENGALI = "noto_sans_bengali"
        const val FONT_NOTO_SANS_BENGALI = "noto_sans_bengali"
        const val FONT_HIND_SILIGURI = "hind_siliguri"
        const val FONT_APONA_LOHIT = "apona_lohit"
        const val FONT_BENSEN = "bensen"
        const val FONT_GALADA = "galada"
        const val FONT_ATMA = "atma"
        const val FONT_MINA = "mina"
        const val FONT_SHONAR_BANGLA = "shonar_bangla"
        const val FONT_SUTONNY = "sutonny"
        const val FONT_CHARU_CHANDAN = "charu_chandan"
        const val FONT_SAGAR = "sagar"
        const val FONT_ADOR = "ador"

        const val FONT_ROBOTO = "roboto"
        const val FONT_INTER = "inter"
        const val FONT_POPPINS = "poppins"
        const val FONT_OUTFIT = "outfit"
        const val FONT_MONTSERRAT = "montserrat"

        const val FONT_CUSTOM = "custom"

        const val ACCENT_CYAN = "CYAN"
        const val ACCENT_EMERALD = "EMERALD"
        const val ACCENT_ORANGE = "ORANGE"
        const val ACCENT_VIOLET = "VIOLET"
        const val ACCENT_ROSE = "ROSE"
        const val ACCENT_AMBER = "AMBER"
        const val ACCENT_PREMIUM_BLUE = "PREMIUM_BLUE"
        const val ACCENT_ROYAL_BLUE = "ROYAL_BLUE"
        const val ACCENT_SKY_BLUE = "SKY_BLUE"
        const val ACCENT_TEAL = "TEAL"
        const val ACCENT_MINT_GREEN = "MINT_GREEN"
        const val ACCENT_PURPLE = "PURPLE"
        const val ACCENT_LAVENDER = "LAVENDER"
        const val ACCENT_PINK = "PINK"
        const val ACCENT_CORAL = "CORAL"
        const val ACCENT_GOLD = "GOLD"
        const val ACCENT_RED = "RED"
        const val ACCENT_NEUTRAL_SLATE = "NEUTRAL_SLATE"

        const val ACCENT_MODE_SINGLE = "single"
        const val ACCENT_MODE_MULTIPLE = "multiple"

        const val ACCENT_COMPONENT_CARDS = "cards"
        const val ACCENT_COMPONENT_NAV = "nav"
        const val ACCENT_COMPONENT_ICONS = "icons"
        const val ACCENT_COMPONENT_CONTROLS = "controls"
        const val ACCENT_COMPONENT_BUTTONS = "buttons"

        const val THEME_LIPIKA_GRADIENT = "lipika_gradient"
        const val THEME_FROSTED_GLASS = "frosted_glass"
        const val THEME_DEFAULT_DARK = "default_dark"
        const val THEME_AMOLED_BLACK = "amoled_black"
        const val THEME_TOKYO_NIGHT = "tokyo_night"
        const val THEME_NORD = "nord"
        const val THEME_DEFAULT_LIGHT = "default_light"

        const val ONE_HANDED_OFF = "off"
        const val ONE_HANDED_LEFT = "left"
        const val ONE_HANDED_RIGHT = "right"

        const val DELETE_MODE_WORD = "word"
        const val DELETE_MODE_CHARACTER = "character"

        const val VIBRATION_TYPE_HAPTIC = "haptic"
        const val VIBRATION_TYPE_VIBRATION = "vibration"

        const val SOUND_PROFILE_DEFAULT = "default"
        const val SOUND_PROFILE_MECHANICAL = "mechanical"
        const val SOUND_PROFILE_TYPEWRITER = "typewriter"
        const val SOUND_PROFILE_BUBBLE = "bubble"

        const val SUGGESTION_BAR_MODE_NORMAL = "normal"
        const val SUGGESTION_BAR_MODE_SCROLLABLE = "scrollable"

        const val APP_LANG_EN = "en"
        const val APP_LANG_BN = "bn"

        const val LAYOUT_EN_QWERTY = "en_qwerty"
        const val LAYOUT_BN_BORNO = "bn_borno"
        const val LAYOUT_BN_AVRO = "bn_avro"
        const val LAYOUT_BN_PROBHAT = "bn_probhat"
        const val LAYOUT_BN_NATIONAL = "bn_national"
        const val LAYOUT_BN_EASY = "bn_easy"
        const val LAYOUT_AR_ARABIC = "ar_arabic"
    }
}
