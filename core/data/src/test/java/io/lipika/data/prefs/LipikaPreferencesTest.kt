package io.lipika.data.prefs

import org.junit.Assert.assertEquals
import org.junit.Test

class LipikaPreferencesTest {

    @Test
    fun testSuggestionBarModeDefaultsAndConstants() {
        val prefs = LipikaPreferences()
        assertEquals(LipikaPreferences.SUGGESTION_BAR_MODE_NORMAL, prefs.suggestionBarMode)
        assertEquals("normal", LipikaPreferences.SUGGESTION_BAR_MODE_NORMAL)
        assertEquals("scrollable", LipikaPreferences.SUGGESTION_BAR_MODE_SCROLLABLE)

        val scrollablePrefs = prefs.copy(suggestionBarMode = LipikaPreferences.SUGGESTION_BAR_MODE_SCROLLABLE)
        assertEquals(LipikaPreferences.SUGGESTION_BAR_MODE_SCROLLABLE, scrollablePrefs.suggestionBarMode)
    }

    @Test
    fun testPhoneticAutoCorrectDefault() {
        val prefs = LipikaPreferences()
        assertEquals(true, prefs.phoneticAutoCorrect)

        val disabledPrefs = prefs.copy(phoneticAutoCorrect = false)
        assertEquals(false, disabledPrefs.phoneticAutoCorrect)
    }

    @Test
    fun testAppearanceDefaultsAndEffectiveTheming() {
        val prefs = LipikaPreferences()
        assertEquals(LipikaPreferences.DISPLAY_MODE_SYSTEM, prefs.displayMode)
        assertEquals(LipikaPreferences.APP_THEME_SYSTEM, prefs.appTheme)
        assertEquals(LipikaPreferences.KEYBOARD_THEME_DARK, prefs.keyboardThemeMode)
        assertEquals(LipikaPreferences.DARK_STYLE_DARK_GRAY, prefs.darkStyle)
        assertEquals(LipikaPreferences.SCHEDULE_ALWAYS_DARK, prefs.themeSchedule)

        // Effective Dark Mode when Schedule is Always Light / Always Dark
        val lightSchedule = prefs.copy(themeSchedule = LipikaPreferences.SCHEDULE_ALWAYS_LIGHT)
        assertEquals(false, lightSchedule.resolveEffectiveIsDark(systemInDark = true))

        val darkSchedule = prefs.copy(themeSchedule = LipikaPreferences.SCHEDULE_ALWAYS_DARK)
        assertEquals(true, darkSchedule.resolveEffectiveIsDark(systemInDark = false))

        // Effective Keyboard Theme
        val lightKb = prefs.copy(keyboardThemeMode = LipikaPreferences.KEYBOARD_THEME_LIGHT)
        assertEquals(LipikaPreferences.THEME_DEFAULT_LIGHT, lightKb.effectiveKeyboardTheme())

        val pureBlackKb = prefs.copy(
            keyboardThemeMode = LipikaPreferences.KEYBOARD_THEME_DARK,
            darkStyle = LipikaPreferences.DARK_STYLE_PURE_BLACK
        )
        assertEquals(LipikaPreferences.THEME_PURE_BLACK, pureBlackKb.effectiveKeyboardTheme())

        val amoledKb = prefs.copy(
            keyboardThemeMode = LipikaPreferences.KEYBOARD_THEME_DARK,
            darkStyle = LipikaPreferences.DARK_STYLE_AMOLED
        )
        assertEquals(LipikaPreferences.THEME_AMOLED_BLACK, amoledKb.effectiveKeyboardTheme())

        val customKb = prefs.copy(
            keyboardThemeMode = LipikaPreferences.KEYBOARD_THEME_CUSTOM,
            theme = "custom_theme_123"
        )
        assertEquals("custom_theme_123", customKb.effectiveKeyboardTheme())
    }
}
