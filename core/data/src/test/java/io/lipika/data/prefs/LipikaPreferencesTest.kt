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
}
