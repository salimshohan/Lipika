package io.lipika.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.text.TextStyle
import io.lipika.theme.KeyboardFontFamily
import io.lipika.theme.LipikaTypography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import io.lipika.app.ui.onboarding.OnboardingScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import io.lipika.app.ui.settings.AboutScreen
import io.lipika.app.ui.settings.AppearanceScreen
import io.lipika.app.ui.settings.AppearanceThemesScreen
import io.lipika.app.ui.settings.BackupRestoreScreen
import io.lipika.app.ui.settings.ClipboardSettingsScreen
import io.lipika.app.ui.settings.FontsScreen
import io.lipika.app.ui.settings.CorrectionsSuggestionsScreen
import io.lipika.app.ui.settings.SmartTypingScreen
import io.lipika.app.ui.settings.SoundVibrationScreen
import io.lipika.app.ui.settings.DarkSettingsColors
import io.lipika.app.ui.settings.KeyboardLayoutsScreen
import io.lipika.app.ui.settings.LightSettingsColors
import io.lipika.app.ui.settings.LocalSettingsColors
import io.lipika.app.ui.settings.PreferencesScreen
import io.lipika.app.ui.settings.SettingsMainScreen
import io.lipika.app.ui.settings.SettingsScreen
import io.lipika.app.ui.settings.SettingsUnifiedTopBar
import io.lipika.app.ui.settings.TypingStatisticsScreen
import io.lipika.app.ui.settings.VoiceTypingScreen
import io.lipika.feature.ime.clipboard.SandboxedClipboardManager
import io.lipika.data.dictionary.UserDictionaryRepository
import io.lipika.data.layout.LayoutRepository
import io.lipika.data.prefs.LipikaPreferences
import io.lipika.data.prefs.LipikaPreferencesRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {

    private var isImeEnabledState by mutableStateOf(false)
    private var isImeSelectedState by mutableStateOf(false)
    private var requestedNavigationTarget by mutableStateOf<String?>(null)
    private var pendingThemeImportUri by mutableStateOf<android.net.Uri?>(null)
    private lateinit var preferencesRepo: LipikaPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            LayoutRepository.getInstance(applicationContext).preload()
        }
        handleIncomingIntent(intent)
        preferencesRepo = LipikaPreferencesRepository.getInstance(this)
        updateImeStatus()

        setContent {
            val preferences by preferencesRepo.preferencesFlow.collectAsState(
                initial = preferencesRepo.getInitialPreferences()
            )

            val systemInDark = isSystemInDarkTheme()
            val isDark = remember(
                preferences.displayMode,
                preferences.appTheme,
                preferences.themeSchedule,
                preferences.customScheduleStartHour,
                preferences.customScheduleEndHour,
                systemInDark
            ) {
                preferences.resolveEffectiveIsDark(systemInDark)
            }
            fun resolveColorToken(raw: String?, fallback: Color): Color {
                if (raw.isNullOrBlank()) return fallback
                if (raw.startsWith("#")) {
                    return runCatching { Color(android.graphics.Color.parseColor(raw)) }.getOrElse { fallback }
                }
                return when (raw) {
                    LipikaPreferences.ACCENT_CYAN -> Color(0xFF06B6D4)
                    LipikaPreferences.ACCENT_ROYAL_BLUE -> Color(0xFF2563EB)
                    LipikaPreferences.ACCENT_EMERALD -> Color(0xFF10B981)
                    LipikaPreferences.ACCENT_MINT_GREEN -> Color(0xFF84CC16)
                    LipikaPreferences.ACCENT_ORANGE -> Color(0xFFF97316)
                    LipikaPreferences.ACCENT_GOLD -> Color(0xFFEAB308)
                    LipikaPreferences.ACCENT_RED -> Color(0xFFEF4444)
                    LipikaPreferences.ACCENT_PINK -> Color(0xFFEC4899)
                    LipikaPreferences.ACCENT_PURPLE -> Color(0xFF7C3AED)
                    LipikaPreferences.ACCENT_VIOLET -> Color(0xFF6366F1)
                    LipikaPreferences.ACCENT_PREMIUM_BLUE -> Color(0xFF0284C7)
                    LipikaPreferences.ACCENT_SKY_BLUE -> Color(0xFF0EA5E9)
                    LipikaPreferences.ACCENT_TEAL -> Color(0xFF0D9488)
                    LipikaPreferences.ACCENT_LAVENDER -> Color(0xFF8B5CF6)
                    LipikaPreferences.ACCENT_ROSE -> Color(0xFFF43F5E)
                    LipikaPreferences.ACCENT_CORAL -> Color(0xFFF97316)
                    LipikaPreferences.ACCENT_AMBER -> Color(0xFFEAB308)
                    LipikaPreferences.ACCENT_NEUTRAL_SLATE -> Color(0xFF475569)
                    else -> fallback
                }
            }

            val defaultAccent = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
            val dynamicAccent = when {
                preferences.appTheme == LipikaPreferences.APP_THEME_DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val dynamicScheme = if (isDark) dynamicDarkColorScheme(this@MainActivity) else dynamicLightColorScheme(this@MainActivity)
                    dynamicScheme.primary
                }
                else -> resolveColorToken(preferences.accentColor, defaultAccent)
            }
            val isMulti = preferences.accentMode == LipikaPreferences.ACCENT_MODE_MULTIPLE
            val multiMap = if (isMulti) preferences.getMultipleAccentsMap() else emptyMap()
            val cardAccent = if (isMulti) resolveColorToken(multiMap[LipikaPreferences.ACCENT_COMPONENT_CARDS], dynamicAccent) else dynamicAccent
            val navAccent = if (isMulti) resolveColorToken(multiMap[LipikaPreferences.ACCENT_COMPONENT_NAV], dynamicAccent) else dynamicAccent
            val iconAccent = if (isMulti) resolveColorToken(multiMap[LipikaPreferences.ACCENT_COMPONENT_ICONS], dynamicAccent) else dynamicAccent
            val controlAccent = if (isMulti) resolveColorToken(multiMap[LipikaPreferences.ACCENT_COMPONENT_CONTROLS], dynamicAccent) else dynamicAccent
            val buttonAccent = if (isMulti) resolveColorToken(multiMap[LipikaPreferences.ACCENT_COMPONENT_BUTTONS], dynamicAccent) else dynamicAccent

            val baseColors = if (isDark) {
                when (preferences.darkStyle) {
                    LipikaPreferences.DARK_STYLE_PURE_BLACK -> DarkSettingsColors.copy(
                        background = Color(0xFF000000),
                        cardBackground = Color(0xFF0A0A0A),
                        cardBorder = Color(0xFF1F1F1F)
                    )
                    LipikaPreferences.DARK_STYLE_AMOLED -> DarkSettingsColors.copy(
                        background = Color(0xFF000000),
                        cardBackground = Color(0xFF121214),
                        cardBorder = Color(0xFF202024)
                    )
                    else -> DarkSettingsColors
                }
            } else {
                LightSettingsColors
            }
            val settingsColors = baseColors.copy(
                accent = dynamicAccent,
                sectionTitle = dynamicAccent,
                cardAccent = cardAccent,
                navAccent = navAccent,
                iconAccent = iconAccent,
                controlAccent = controlAccent,
                buttonAccent = buttonAccent,
                accentMode = preferences.accentMode
            )

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as? android.app.Activity)?.window
                    if (window != null) {
                        val insetsController = WindowCompat.getInsetsController(window, view)
                        insetsController.isAppearanceLightStatusBars = !isDark
                        insetsController.isAppearanceLightNavigationBars = !isDark
                    }
                }
            }

            val currentFontFamily = remember(preferences.selectedFont, preferences.customFontPath) {
                io.lipika.theme.resolveKeyboardFontFamily(preferences.selectedFont, preferences.customFontPath)
            }
            val appTypography = remember(currentFontFamily) {
                val d = androidx.compose.material3.Typography()
                androidx.compose.material3.Typography(
                    displayLarge = d.displayLarge.copy(fontFamily = currentFontFamily),
                    displayMedium = d.displayMedium.copy(fontFamily = currentFontFamily),
                    displaySmall = d.displaySmall.copy(fontFamily = currentFontFamily),
                    headlineLarge = d.headlineLarge.copy(fontFamily = currentFontFamily),
                    headlineMedium = d.headlineMedium.copy(fontFamily = currentFontFamily),
                    headlineSmall = d.headlineSmall.copy(fontFamily = currentFontFamily),
                    titleLarge = d.titleLarge.copy(fontFamily = currentFontFamily),
                    titleMedium = d.titleMedium.copy(fontFamily = currentFontFamily),
                    titleSmall = d.titleSmall.copy(fontFamily = currentFontFamily),
                    bodyLarge = d.bodyLarge.copy(fontFamily = currentFontFamily),
                    bodyMedium = d.bodyMedium.copy(fontFamily = currentFontFamily),
                    bodySmall = d.bodySmall.copy(fontFamily = currentFontFamily),
                    labelLarge = d.labelLarge.copy(fontFamily = currentFontFamily),
                    labelMedium = d.labelMedium.copy(fontFamily = currentFontFamily),
                    labelSmall = d.labelSmall.copy(fontFamily = currentFontFamily)
                )
            }

            CompositionLocalProvider(
                LocalSettingsColors provides settingsColors,
                LocalTextStyle provides TextStyle(fontFamily = currentFontFamily)
            ) {
                MaterialTheme(
                    colorScheme = if (isDark) {
                        darkColorScheme(
                            background = settingsColors.background,
                            surface = settingsColors.cardBackground,
                            primary = settingsColors.accent,
                            onPrimary = Color(0xFF0F141C),
                            onSurface = settingsColors.textPrimary
                        )
                    } else {
                        lightColorScheme(
                            background = settingsColors.background,
                            surface = settingsColors.cardBackground,
                            primary = settingsColors.accent,
                            onPrimary = Color(0xFFFFFFFF),
                            onSurface = settingsColors.textPrimary
                        )
                    },
                    typography = appTypography
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // Ambient Frosted Aurora 1: Top-Right Cyan/Azure Glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isDark) Color(0x3506B6D4) else Color(0x2838BDF8),
                                            Color.Transparent
                                        ),
                                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.12f),
                                        radius = canvasWidth * 0.7f
                                    )
                                )

                                // Ambient Frosted Aurora 2: Mid-Left Deep Violet/Indigo Glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isDark) Color(0x306366F1) else Color(0x22818CF8),
                                            Color.Transparent
                                        ),
                                        center = Offset(canvasWidth * 0.15f, canvasHeight * 0.45f),
                                        radius = canvasWidth * 0.75f
                                    )
                                )

                                // Ambient Frosted Aurora 3: Bottom-Right Emerald/Teal Glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            if (isDark) Color(0x2510B981) else Color(0x1F34D399),
                                            Color.Transparent
                                        ),
                                        center = Offset(canvasWidth * 0.8f, canvasHeight * 0.82f),
                                        radius = canvasWidth * 0.65f
                                    )
                                )
                            },
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                            updateImeStatus()
                        }

                        LaunchedEffect(Unit) {
                            while (true) {
                                updateImeStatus()
                                delay(300L)
                            }
                        }

                        LipikaSettingsApp(
                            preferences = preferences,
                            preferencesRepo = preferencesRepo,
                            isImeEnabled = isImeEnabledState,
                            isImeSelected = isImeSelectedState,
                            navigationTarget = requestedNavigationTarget,
                            onNavigationTargetConsumed = { requestedNavigationTarget = null },
                            pendingThemeImportUri = pendingThemeImportUri,
                            onClearPendingThemeImportUri = { pendingThemeImportUri = null },
                            onOpenSettings = {
                                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                            },
                            onSwitchIme = {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.showInputMethodPicker()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        var importUri: android.net.Uri? = null
        if (Intent.ACTION_VIEW == intent.action) {
            importUri = intent.data
        } else if (Intent.ACTION_SEND == intent.action && intent.hasExtra(Intent.EXTRA_STREAM)) {
            @Suppress("DEPRECATION")
            importUri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
        if (importUri != null) {
            pendingThemeImportUri = importUri
            requestedNavigationTarget = "themes"
            return
        }
        val target = intent.getStringExtra("navigate_to")
            ?: if (intent.action == "io.lipika.action.THEMES") "themes" else null
        if (target != null) {
            requestedNavigationTarget = target
        }
    }

    override fun onResume() {
        super.onResume()
        updateImeStatus()
    }

    private fun updateImeStatus() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledMethods = imm.enabledInputMethodList
        isImeEnabledState = enabledMethods.any { it.packageName == packageName }

        val defaultIme = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        isImeSelectedState = defaultIme != null && defaultIme.contains(packageName)
    }
}

@Composable
fun LipikaSettingsApp(
    preferences: LipikaPreferences,
    preferencesRepo: LipikaPreferencesRepository,
    isImeEnabled: Boolean,
    isImeSelected: Boolean,
    navigationTarget: String? = null,
    onNavigationTargetConsumed: () -> Unit = {},
    pendingThemeImportUri: android.net.Uri? = null,
    onClearPendingThemeImportUri: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onSwitchIme: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val isSetupFinished = preferences.setupCompleted || preferencesRepo.isSetupCompletedSync() || (isImeEnabled && isImeSelected)

    LaunchedEffect(isImeEnabled, isImeSelected) {
        if (isImeEnabled && isImeSelected && !preferences.setupCompleted) {
            preferencesRepo.updateSetupCompleted(true)
        }
    }

    val initialScreen = remember(isSetupFinished) {
        val target = navigationTarget
            ?: (context as? android.app.Activity)?.intent?.getStringExtra("navigate_to")
            ?: if ((context as? android.app.Activity)?.intent?.action == "io.lipika.action.THEMES") "themes" else null
        if (target == "layouts") {
            SettingsScreen.KeyboardLayouts
        } else if (target == "appearance") {
            SettingsScreen.Appearance
        } else if (target == "themes" || pendingThemeImportUri != null) {
            SettingsScreen.AppearanceThemes()
        } else if (target == "fonts" || target == "font") {
            SettingsScreen.Fonts
        } else if (target == "sound" || target == "vibration" || target == "sound_vibration") {
            SettingsScreen.SoundVibration
        } else if (target == "clipboard") {
            SettingsScreen.Clipboard
        } else if (target == "preferences") {
            SettingsScreen.Preferences
        } else if (target == "smart_typing" || target == "typing" || target == "corrections" || target == "suggestions") {
            SettingsScreen.SmartTyping
        } else if (!isSetupFinished) {
            SettingsScreen.Onboarding
        } else {
            SettingsScreen.Main
        }
    }
    var currentScreen by remember(initialScreen) { mutableStateOf<SettingsScreen>(initialScreen) }
    var backStack by remember { mutableStateOf<List<SettingsScreen>>(emptyList()) }

    val isMainScreen = currentScreen == SettingsScreen.Main

    val view = androidx.compose.ui.platform.LocalView.current
    val isDark = isSystemInDarkTheme()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window
            if (window != null) {
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = if (isMainScreen) false else !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    LaunchedEffect(navigationTarget) {
        if (navigationTarget != null) {
            when (navigationTarget) {
                "appearance" -> {
                    if (currentScreen != SettingsScreen.Appearance) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.Appearance
                    }
                }
                "themes" -> {
                    if (currentScreen !is SettingsScreen.AppearanceThemes) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.AppearanceThemes()
                    }
                }
                "layouts" -> {
                    if (currentScreen != SettingsScreen.KeyboardLayouts) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.KeyboardLayouts
                    }
                }
                "preferences" -> {
                    if (currentScreen != SettingsScreen.Preferences) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.Preferences
                    }
                }
                "fonts", "font" -> {
                    if (currentScreen != SettingsScreen.Fonts) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.Fonts
                    }
                }
                "sound", "vibration", "sound_vibration" -> {
                    if (currentScreen != SettingsScreen.SoundVibration) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.SoundVibration
                    }
                }
                "clipboard" -> {
                    if (currentScreen != SettingsScreen.Clipboard) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.Clipboard
                    }
                }
                "smart_typing", "typing", "corrections", "suggestions" -> {
                    if (currentScreen != SettingsScreen.SmartTyping) {
                        backStack = listOf(SettingsScreen.Main)
                        currentScreen = SettingsScreen.SmartTyping
                    }
                }
            }
            onNavigationTargetConsumed()
        }
    }
    var predictiveProgress by remember { mutableFloatStateOf(0f) }
    var isPredictiveBackActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val navigateTo: (SettingsScreen) -> Unit = { screen ->
        backStack = backStack + currentScreen
        currentScreen = screen
    }

    val navigateBack: () -> Unit = {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.last()
            backStack = backStack.dropLast(1)
        } else {
            currentScreen = SettingsScreen.Main
        }
    }

    PredictiveBackHandler(enabled = currentScreen != SettingsScreen.Main) { progress ->
        try {
            isPredictiveBackActive = true
            progress.collect { backEvent ->
                predictiveProgress = backEvent.progress
            }
            navigateBack()
        } catch (e: CancellationException) {
            // Cancelled
        } finally {
            predictiveProgress = 0f
            isPredictiveBackActive = false
        }
    }

    val predictiveModifier = if (isPredictiveBackActive) {
        Modifier.graphicsLayer {
            val scale = 1f - (predictiveProgress * 0.08f)
            scaleX = scale
            scaleY = scale
            translationX = predictiveProgress * 48.dp.toPx()
            alpha = 1f - (predictiveProgress * 0.2f)
            shape = RoundedCornerShape((predictiveProgress * 20f).dp)
            clip = true
        }
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Persistent Top Bar that stays anchored, only transitioning title/back-icon smoothly
        if (!isMainScreen && currentScreen != SettingsScreen.Onboarding) {
            SettingsUnifiedTopBar(
                screen = currentScreen,
                onBack = navigateBack
            )
        }

        // Inner Content changes smoothly without reloading the entire page
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(predictiveModifier)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState == SettingsScreen.Main) {
                        (slideInHorizontally { -it / 3 } + fadeIn(tween(220))) togetherWith
                                (slideOutHorizontally { it } + fadeOut(tween(180)))
                    } else {
                        (slideInHorizontally { it } + fadeIn(tween(220))) togetherWith
                                (slideOutHorizontally { -it / 3 } + fadeOut(tween(180)))
                    }
                },
                label = "SettingsInnerContentTransition"
            ) { screen ->
                when (screen) {
                    is SettingsScreen.Main -> {
                        SettingsMainScreen(
                            preferences = preferences,
                            isImeEnabled = isImeEnabled,
                            isImeSelected = isImeSelected,
                            onOpenSettings = onOpenSettings,
                            onSwitchIme = onSwitchIme,
                            onNavigate = navigateTo
                        )
                    }

                    is SettingsScreen.AppearanceThemes -> {
                        AppearanceThemesScreen(
                            initialTab = screen.initialTab,
                            preferences = preferences,
                            onUpdateTheme = { theme ->
                                scope.launch { preferencesRepo.updateTheme(theme) }
                            },
                            onUpdateHeightScale = { scale ->
                                scope.launch { preferencesRepo.updateKeyboardHeightScale(scale) }
                            },
                            onUpdateShowKeyBorders = { enabled ->
                                scope.launch { preferencesRepo.updateShowKeyBorders(enabled) }
                            },
                            onUpdateShowKeyShadows = { enabled ->
                                scope.launch { preferencesRepo.updateShowKeyShadows(enabled) }
                            },
                            pendingImportUri = pendingThemeImportUri,
                            onClearPendingImportUri = onClearPendingThemeImportUri,
                            onOpenThemeDesigner = { editingThemeId ->
                                navigateTo(SettingsScreen.ThemeDesigner(editingThemeId))
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.Appearance -> {
                        AppearanceScreen(
                            preferences = preferences,
                            onUpdateAccentMode = { mode ->
                                scope.launch { preferencesRepo.updateAccentMode(mode) }
                            },
                            onUpdateAccentColor = { color ->
                                scope.launch { preferencesRepo.updateAccentColor(color) }
                            },
                            onUpdateComponentAccent = { component, color ->
                                scope.launch { preferencesRepo.updateComponentAccent(component, color) }
                            },
                            onResetAccents = {
                                scope.launch {
                                    preferencesRepo.updateAccentMode(LipikaPreferences.ACCENT_MODE_SINGLE)
                                    preferencesRepo.updateAccentColor(LipikaPreferences.ACCENT_CYAN)
                                    preferencesRepo.updateMultipleAccents(emptyMap())
                                }
                            },
                            onUpdateDisplayMode = { mode ->
                                scope.launch { preferencesRepo.updateDisplayMode(mode) }
                            },
                            onUpdateAppTheme = { appTheme ->
                                scope.launch { preferencesRepo.updateAppTheme(appTheme) }
                            },
                            onUpdateKeyboardThemeMode = { mode ->
                                scope.launch {
                                    preferencesRepo.updateKeyboardThemeMode(mode)
                                    val updatedTheme = when (mode) {
                                        LipikaPreferences.KEYBOARD_THEME_LIGHT -> LipikaPreferences.THEME_DEFAULT_LIGHT
                                        LipikaPreferences.KEYBOARD_THEME_DARK -> when (preferences.darkStyle) {
                                            LipikaPreferences.DARK_STYLE_PURE_BLACK -> LipikaPreferences.THEME_PURE_BLACK
                                            LipikaPreferences.DARK_STYLE_AMOLED -> LipikaPreferences.THEME_AMOLED_BLACK
                                            else -> LipikaPreferences.THEME_DEFAULT_DARK
                                        }
                                        else -> preferences.theme
                                    }
                                    preferencesRepo.updateTheme(updatedTheme)
                                }
                            },
                            onUpdateDarkStyle = { style ->
                                scope.launch {
                                    preferencesRepo.updateDarkStyle(style)
                                    if (preferences.keyboardThemeMode == LipikaPreferences.KEYBOARD_THEME_DARK) {
                                        val updatedTheme = when (style) {
                                            LipikaPreferences.DARK_STYLE_PURE_BLACK -> LipikaPreferences.THEME_PURE_BLACK
                                            LipikaPreferences.DARK_STYLE_AMOLED -> LipikaPreferences.THEME_AMOLED_BLACK
                                            else -> LipikaPreferences.THEME_DEFAULT_DARK
                                        }
                                        preferencesRepo.updateTheme(updatedTheme)
                                    }
                                }
                            },
                            onUpdateThemeSchedule = { schedule ->
                                scope.launch { preferencesRepo.updateThemeSchedule(schedule) }
                            },
                            onUpdateCustomScheduleHours = { start, end ->
                                scope.launch { preferencesRepo.updateCustomScheduleHours(start, end) }
                            },
                            onOpenThemeStudio = {
                                navigateTo(SettingsScreen.AppearanceThemes(0))
                            },
                            onBack = navigateBack
                        )
                    }

                                        is SettingsScreen.Fonts -> {
                        FontsScreen(
                            preferences = preferences,
                            onUpdateSelectedFont = { font ->
                                scope.launch { preferencesRepo.updateSelectedFont(font) }
                            },
                            onUpdateSelectedBengaliFont = { font ->
                                scope.launch { preferencesRepo.updateSelectedBengaliFont(font) }
                            },
                            onUpdateCustomFontPath = { path ->
                                scope.launch { preferencesRepo.updateCustomFontPath(path) }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.ThemeDesigner -> {
                        val settingsColors = io.lipika.app.ui.settings.LocalSettingsColors.current
                        val designerFeature = io.lipika.theme.designer.ThemeDesignerRegistry.feature
                        if (designerFeature != null) {
                            designerFeature.Screen(
                                editingThemeId = screen.editingThemeId,
                                colors = io.lipika.theme.designer.ThemeDesignerColors(
                                    background = settingsColors.background,
                                    cardBackground = settingsColors.cardBackground,
                                    cardBorder = settingsColors.cardBorder,
                                    textPrimary = settingsColors.textPrimary,
                                    textSecondary = settingsColors.textSecondary,
                                    accent = settingsColors.accent,
                                    divider = settingsColors.divider,
                                    inputBackground = settingsColors.inputBackground,
                                    inputBorder = settingsColors.inputBorder,
                                    isDark = settingsColors.isDark
                                ),
                                previewConfig = io.lipika.theme.designer.ThemeDesignerPreviewConfig(
                                    showNumberRow = preferences.showNumberRow,
                                    keyboardHeightScale = preferences.keyboardHeightScale,
                                    showKeyBorders = preferences.showKeyBorders,
                                    showKeyShadows = preferences.showKeyShadows
                                ),
                                onBack = navigateBack,
                                onThemeSaved = { savedId ->
                                    scope.launch { preferencesRepo.updateTheme(savedId) }
                                    navigateBack()
                                }
                            )
                        } else {
                            androidx.compose.runtime.LaunchedEffect(Unit) { navigateBack() }
                        }
                    }

                    is SettingsScreen.KeyboardLayouts -> {
                        KeyboardLayoutsScreen(
                            preferences = preferences,
                            onUpdatePrimaryLayout = { layoutId ->
                                scope.launch { preferencesRepo.updatePrimaryLayoutId(layoutId) }
                            },
                            onToggleLayoutEnabled = { layoutId, enabled ->
                                scope.launch { preferencesRepo.toggleLayoutEnabled(layoutId, enabled) }
                            },
                            onReorderEnabledLayouts = { orderedIds ->
                                scope.launch { preferencesRepo.updateEnabledLayoutsOrder(orderedIds) }
                            },
                            onToggleExtraRow = { layoutId, rowId, enabled ->
                                scope.launch { preferencesRepo.setLayoutExtraRowEnabled(layoutId, rowId, enabled) }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.Preferences -> {
                        PreferencesScreen(
                            preferences = preferences,
                            onUpdateVibrate = { enabled ->
                                scope.launch { preferencesRepo.updateVibrateOnKeypress(enabled) }
                            },
                            onUpdateVibrationType = { type ->
                                scope.launch { preferencesRepo.updateVibrationType(type) }
                            },
                            onUpdateVibrationStrength = { strength ->
                                scope.launch { preferencesRepo.updateVibrationStrength(strength) }
                            },
                            onUpdateSound = { enabled ->
                                scope.launch { preferencesRepo.updateSoundOnKeypress(enabled) }
                            },
                            onUpdateLongPressDelay = { delayMs ->
                                scope.launch { preferencesRepo.updateLongPressDelayMs(delayMs) }
                            },
                            onUpdateAccentColor = { color ->
                                scope.launch { preferencesRepo.updateAccentColor(color) }
                            },
                            onUpdateShowNumberRow = { enabled ->
                                scope.launch { preferencesRepo.updateShowNumberRow(enabled) }
                            },
                            onUpdateShowKeyPopups = { enabled ->
                                scope.launch { preferencesRepo.updateShowKeyPopups(enabled) }
                            },
                            onUpdateShowSecondaryKeys = { enabled ->
                                scope.launch { preferencesRepo.updateShowSecondaryKeys(enabled) }
                            },
                            onUpdateCursorTrackpad = { enabled ->
                                scope.launch { preferencesRepo.updateCursorTrackpadEnabled(enabled) }
                            },
                            onUpdateOneHandedMode = { mode ->
                                scope.launch { preferencesRepo.updateOneHandedMode(mode) }
                            },
                            onUpdateLongPressDelete = { enabled ->
                                scope.launch { preferencesRepo.updateLongPressDeleteEnabled(enabled) }
                            },
                            onUpdateSelectiveDeleteMode = { mode ->
                                scope.launch { preferencesRepo.updateSelectiveDeleteMode(mode) }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.SmartTyping, is SettingsScreen.CorrectionsSuggestions -> {
                        SmartTypingScreen(
                            preferences = preferences,
                            onUpdateSuggestionsEnabled = { enabled ->
                                scope.launch { preferencesRepo.updateSuggestionsEnabled(enabled) }
                            },
                            onUpdateNextWordPrediction = { enabled ->
                                scope.launch { preferencesRepo.updateNextWordPrediction(enabled) }
                            },
                            onUpdateEmojiPredictionEnabled = { enabled ->
                                scope.launch { preferencesRepo.updateEmojiPredictionEnabled(enabled) }
                            },
                            onUpdateLearnedWordsEnabled = { enabled ->
                                scope.launch { preferencesRepo.updateLearnedWordsEnabled(enabled) }
                            },
                            onUpdateFilterOffensiveWords = { enabled ->
                                scope.launch { preferencesRepo.updateFilterOffensiveWords(enabled) }
                            },
                            onUpdateAutoCorrection = { enabled ->
                                scope.launch { preferencesRepo.updateAutoCorrection(enabled) }
                            },
                            onUpdatePhoneticAutoCorrect = { enabled ->
                                scope.launch { preferencesRepo.updatePhoneticAutoCorrect(enabled) }
                            },
                            onUpdateSmartSpellChecker = { enabled ->
                                scope.launch { preferencesRepo.updateSmartSpellChecker(enabled) }
                            },
                            onUpdateCustomWordLearning = { enabled ->
                                scope.launch { preferencesRepo.updateCustomWordLearning(enabled) }
                            },
                            onAddCustomWord = { word ->
                                UserDictionaryRepository.getInstance(context).addCustomWord(word)
                            },
                            onDeleteCustomWord = { word ->
                                UserDictionaryRepository.getInstance(context).deleteCustomWord(word)
                            },
                            onDeleteLearnedWords = {
                                UserDictionaryRepository.getInstance(context).clearAll()
                            },
                            getLearnedWords = {
                                UserDictionaryRepository.getInstance(context).getAllLearnedWords(limit = 200)
                            },
                            onUpdateBengaliSmartPhonetic = { enabled ->
                                scope.launch { preferencesRepo.updateBengaliSmartPhonetic(enabled) }
                            },
                            onUpdateBengaliShowRawInputCandidate = { enabled ->
                                scope.launch { preferencesRepo.updateBengaliShowRawInputCandidate(enabled) }
                            },
                            onUpdateGestureTyping = { enabled ->
                                scope.launch { preferencesRepo.updateGestureTypingEnabled(enabled) }
                            },
                            onUpdateShowGestureTrail = { enabled ->
                                scope.launch { preferencesRepo.updateShowGestureTrail(enabled) }
                            },
                            onUpdateAutoCapitalization = { enabled ->
                                scope.launch { preferencesRepo.updateAutoCapitalization(enabled) }
                            },
                            onUpdateDoubleSpacePeriod = { enabled ->
                                scope.launch { preferencesRepo.updateDoubleSpacePeriod(enabled) }
                            },
                            onUpdateAutoSpaceAfterSuggestion = { enabled ->
                                scope.launch { preferencesRepo.updateAutoSpaceAfterSuggestion(enabled) }
                            },
                            onUpdateAutoSpaceAfterPunctuation = { enabled ->
                                scope.launch { preferencesRepo.updateAutoSpaceAfterPunctuation(enabled) }
                            },
                            onUpdateRemoveSpaceBeforePunctuation = { enabled ->
                                scope.launch { preferencesRepo.updateRemoveSpaceBeforePunctuation(enabled) }
                            },
                            onUpdateSuggestionBarMode = { mode ->
                                scope.launch { preferencesRepo.updateSuggestionBarMode(mode) }
                            },
                            onBack = navigateBack
                        )
                    }
                    is SettingsScreen.Clipboard -> {
                        ClipboardSettingsScreen(
                            clipboardManager = SandboxedClipboardManager.getInstance(context),
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.SoundVibration -> {
                        SoundVibrationScreen(
                            preferences = preferences,
                            onUpdateSoundOnKeypress = { enabled ->
                                scope.launch { preferencesRepo.updateSoundOnKeypress(enabled) }
                            },
                            onUpdateSoundVolume = { volume ->
                                scope.launch { preferencesRepo.updateSoundVolume(volume) }
                            },
                            onUpdateSoundProfile = { profile ->
                                scope.launch { preferencesRepo.updateSoundProfile(profile) }
                            },
                            onUpdateSoundDistinctKeys = { enabled ->
                                scope.launch { preferencesRepo.updateSoundDistinctKeys(enabled) }
                            },
                            onUpdateVibrateOnKeypress = { enabled ->
                                scope.launch { preferencesRepo.updateVibrateOnKeypress(enabled) }
                            },
                            onUpdateVibrationType = { type ->
                                scope.launch { preferencesRepo.updateVibrationType(type) }
                            },
                            onUpdateVibrationStrength = { strength ->
                                scope.launch { preferencesRepo.updateVibrationStrength(strength) }
                            },
                            onUpdateVibrationDuration = { duration ->
                                scope.launch { preferencesRepo.updateVibrationDurationMs(duration) }
                            },
                            onUpdateVibrateOnLongPress = { enabled ->
                                scope.launch { preferencesRepo.updateVibrateOnLongPress(enabled) }
                            },
                            onUpdateVibrateOnSpaceSwipe = { enabled ->
                                scope.launch { preferencesRepo.updateVibrateOnSpaceSwipe(enabled) }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.VoiceTyping -> {
                        VoiceTypingScreen(
                            preferences = preferences,
                            onUpdateVoiceTypingEnabled = { enabled ->
                                scope.launch { preferencesRepo.updateVoiceTypingEnabled(enabled) }
                            },
                            onUpdateVoiceTypingProvider = { provider ->
                                scope.launch { preferencesRepo.updateVoiceTypingProvider(provider) }
                            }
                        )
                    }

                    is SettingsScreen.TypingStatistics -> {
                        TypingStatisticsScreen(
                            preferences = preferences,
                            onResetStats = {
                                scope.launch { preferencesRepo.resetStatistics() }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.BackupRestore -> {
                        BackupRestoreScreen(
                            clipboardManager = SandboxedClipboardManager.getInstance(context),
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.About -> {
                        AboutScreen(
                            onResetDefaults = {
                                scope.launch { preferencesRepo.resetToDefaults() }
                            },
                            onBack = navigateBack
                        )
                    }

                    is SettingsScreen.Onboarding -> {
                        OnboardingScreen(
                            preferences = preferences,
                            preferencesRepo = preferencesRepo,
                            isImeEnabled = isImeEnabled,
                            isImeSelected = isImeSelected,
                            onOpenSettings = onOpenSettings,
                            onSwitchIme = onSwitchIme,
                            onComplete = {
                                currentScreen = SettingsScreen.Main
                                backStack = emptyList()
                            },
                            onOpenKeyboardLayouts = {
                                navigateTo(SettingsScreen.KeyboardLayouts)
                            }
                        )
                    }
                }
            }
        }
    }
}
