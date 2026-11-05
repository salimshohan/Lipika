package io.kiebord.app.ui.settings

/**
 * Defines the navigation destinations within Lipika Settings.
 */
sealed class SettingsScreen {
    data object Main : SettingsScreen()
    data class AppearanceThemes(val initialTab: Int = 0) : SettingsScreen()
    data object Fonts : SettingsScreen()
    data class ThemeDesigner(val editingThemeId: String? = null) : SettingsScreen()
    data object KeyboardLayouts : SettingsScreen()
    data object Preferences : SettingsScreen()
    data object SmartTyping : SettingsScreen()
    data object CorrectionsSuggestions : SettingsScreen()
    data object Clipboard : SettingsScreen()
    data object VoiceTyping : SettingsScreen()
    data object BackupRestore : SettingsScreen()
    data object About : SettingsScreen()
    data object Onboarding : SettingsScreen()
}
