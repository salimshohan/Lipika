package io.lipika.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.lipika.core.theme.R

val KeyboardFontFamily = FontFamily(
    Font(R.font.keyboard_font, FontWeight.Normal),
    Font(R.font.keyboard_font, FontWeight.Medium),
    Font(R.font.keyboard_font, FontWeight.SemiBold),
    Font(R.font.keyboard_font, FontWeight.Bold)
)

val LipikaTypography = androidx.compose.material3.Typography().let { defaultTypography ->
    androidx.compose.material3.Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = KeyboardFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = KeyboardFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = KeyboardFontFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = KeyboardFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = KeyboardFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = KeyboardFontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = KeyboardFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = KeyboardFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = KeyboardFontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = KeyboardFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = KeyboardFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = KeyboardFontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = KeyboardFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = KeyboardFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = KeyboardFontFamily)
    )
}

val LocalKeyboardColorScheme = staticCompositionLocalOf {
    KeyboardColorScheme.DefaultDark
}

val LocalKeyboardFontFamily = staticCompositionLocalOf {
    KeyboardFontFamily
}

fun resolveKeyboardFontFamily(
    selectedFont: String,
    customFontPath: String? = null
): FontFamily {
    return when (selectedFont) {
        "tw_cent_mt" -> FontFamily(Font(R.font.tw_cent_mt, FontWeight.Normal))
        "kalpurush" -> FontFamily(Font(R.font.kalpurush, FontWeight.Normal))
        "segoe_ui" -> FontFamily(Font(R.font.segoe_ui, FontWeight.Normal))
        "georgia" -> FontFamily(Font(R.font.georgia, FontWeight.Normal))
        "consolas" -> FontFamily(Font(R.font.consolas, FontWeight.Normal))
        "candara" -> FontFamily(Font(R.font.candara, FontWeight.Normal))
        "comic_sans" -> FontFamily(Font(R.font.comic_sans, FontWeight.Normal))
        "trebuchet" -> FontFamily(Font(R.font.trebuchet, FontWeight.Normal))
        "verdana" -> FontFamily(Font(R.font.verdana, FontWeight.Normal))
        "calibri" -> FontFamily(Font(R.font.calibri, FontWeight.Normal))
        "corbel" -> FontFamily(Font(R.font.corbel, FontWeight.Normal))
        "custom" -> {
            if (!customFontPath.isNullOrBlank()) {
                try {
                    val file = java.io.File(customFontPath)
                    if (file.exists() && file.isFile && file.canRead()) {
                        val typeface = android.graphics.Typeface.createFromFile(file)
                        if (typeface != null) FontFamily(typeface) else FontFamily(Font(R.font.tw_cent_mt))
                    } else FontFamily(Font(R.font.tw_cent_mt))
                } catch (_: Throwable) {
                    FontFamily(Font(R.font.tw_cent_mt))
                }
            } else {
                FontFamily(Font(R.font.tw_cent_mt))
            }
        }
        else -> FontFamily(Font(R.font.tw_cent_mt, FontWeight.Normal))
    }
}

object LipikaTheme {
    val colorScheme: KeyboardColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalKeyboardColorScheme.current

    val fontFamily: FontFamily
        @Composable
        @ReadOnlyComposable
        get() = LocalKeyboardFontFamily.current
}

@Composable
fun LipikaTheme(
    colorScheme: KeyboardColorScheme = KeyboardColorScheme.DefaultDark,
    fontFamily: FontFamily? = null,
    selectedFont: String = "default",
    customFontPath: String = "",
    content: @Composable () -> Unit
) {
    val resolvedFontFamily = fontFamily
        ?: if (selectedFont != "default") resolveKeyboardFontFamily(selectedFont, customFontPath)
        else colorScheme.effectiveFontFamily ?: KeyboardFontFamily
    CompositionLocalProvider(
        LocalKeyboardColorScheme provides colorScheme,
        LocalKeyboardFontFamily provides resolvedFontFamily,
        LocalTextStyle provides TextStyle(fontFamily = resolvedFontFamily),
        content = content
    )
}
