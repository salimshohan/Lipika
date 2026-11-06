package io.lipika.feature.themedesigner

import androidx.compose.runtime.Composable
import io.lipika.theme.designer.ThemeDesignerColors
import io.lipika.theme.designer.ThemeDesignerFeature
import io.lipika.theme.designer.ThemeDesignerPreviewConfig

/**
 * Runtime entry point discovered reflectively by [io.lipika.theme.designer.ThemeDesignerRegistry].
 *
 * Keeping this in `:feature:themedesigner` allows the entire Theme Studio to be excluded from a
 * future "lite" flavor simply by omitting the Gradle dependency on `:feature:themedesigner`.
 */
object ThemeDesignerFeatureImpl : ThemeDesignerFeature {

    @Composable
    override fun Screen(
        editingThemeId: String?,
        colors: ThemeDesignerColors,
        previewConfig: ThemeDesignerPreviewConfig,
        onBack: () -> Unit,
        onThemeSaved: (themeId: String) -> Unit
    ) {
        ThemeDesignerScreenContent(
            editingThemeId = editingThemeId,
            colors = colors,
            previewConfig = previewConfig,
            onBack = onBack,
            onThemeSaved = onThemeSaved
        )
    }
}

