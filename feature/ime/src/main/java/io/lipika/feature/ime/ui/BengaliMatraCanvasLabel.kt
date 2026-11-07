package io.lipika.feature.ime.ui

import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.res.ResourcesCompat
import io.lipika.common.util.BengaliMatraUtils
import io.lipika.core.theme.R

/**
 * Dedicated Canvas text-drawing composable that renders standalone Bengali combining marks
 * (Kars/Matras, Hasanta, diacritics) on Canvas without the unwanted Unicode dotted circle
 * (◌ / U+25CC).
 */
@Composable
fun BengaliMatraCanvasLabel(
    text: String,
    color: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f,
    typeface: Typeface? = null
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val fontSizePx = with(density) { fontSize.toPx() }
    val effectiveColor = if (alpha < 1.0f) color.copy(alpha = color.alpha * alpha) else color

    val textPaint = remember(effectiveColor, fontSizePx, typeface) {
        TextPaint().apply {
            isAntiAlias = true
            isSubpixelText = true
            this.color = effectiveColor.toArgb()
            this.textSize = fontSizePx
            this.typeface = typeface ?: try {
                ResourcesCompat.getFont(context, R.font.kalpurush)
            } catch (_: Throwable) {
                Typeface.SANS_SERIF
            }
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val fontMetrics = textPaint.fontMetrics
        val baselineY = (height - fontMetrics.ascent - fontMetrics.descent) / 2f
        val centerX = width / 2f

        BengaliMatraUtils.drawCombiningMark(
            canvas = drawContext.canvas.nativeCanvas,
            text = text,
            centerX = centerX,
            baselineY = baselineY,
            paint = textPaint
        )
    }
}
