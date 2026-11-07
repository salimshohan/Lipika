package io.lipika.common.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

/**
 * Utility for detecting standalone Bengali combining marks (dependent vowel signs / Kars,
 * Hasanta, and diacritics) and rendering them on Android Canvas without the unwanted
 * Unicode dotted circle (◌ / U+25CC) using Path Subtraction (Path.Op.DIFFERENCE).
 */
object BengaliMatraUtils {

    /**
     * Identifies if a character is a Bengali dependent vowel sign (Kar/Matra), Hasanta, or diacritic:
     * - \u0981..\u0983 (ঁ Chandrabindu, ং Anusvara, ঃ Visarga)
     * - \u09BC (় Nukta)
     * - \u09BE..\u09CD (া, ি, ী, ু, ূ, ৃ, ৄ, ে, ৈ, ো, ৌ, ্)
     * - \u09D7 (ৗ Au length mark)
     * - \u09E2..\u09E3 (ৢ, ৣ)
     */
    fun isBengaliCombiningChar(c: Char): Boolean {
        return (c in '\u0981'..'\u0983') ||
                c == '\u09BC' ||
                (c in '\u09BE'..'\u09CD') ||
                c == '\u09D7' ||
                (c in '\u09E2'..'\u09E3')
    }

    /**
     * Returns true if the string length is 1 and its char code is in the Bengali
     * dependent vowel / diacritic ranges (\u0981..\u0983, \u09BC, \u09BE..\u09CD, or \u09D7).
     */
    fun isBengaliCombiningMark(text: String?): Boolean {
        if (text == null || text.length != 1) return false
        val c = text[0]
        return (c in '\u0981'..'\u0983') ||
                c == '\u09BC' ||
                (c in '\u09BE'..'\u09CD') ||
                c == '\u09D7' ||
                (c in '\u09E2'..'\u09E3')
    }

    /**
     * Returns true if the string is a standalone Bengali combining mark (i.e. contains dependent vowel
     * signs, Hasanta or diacritics without an independent consonant or vowel base).
     */
    fun isStandaloneBengaliCombiningMark(str: String?): Boolean {
        if (str.isNullOrEmpty()) return false
        if (str.length == 1) return isBengaliCombiningMark(str)
        val clean = str.filter { it != '\u200C' && it != '\u200D' && it != ' ' && it != '\u00A0' && it != '\u25CC' }
        return clean.isNotEmpty() && clean.all { isBengaliCombiningChar(it) }
    }

    /**
     * Prepares a string for Canvas / Text rendering with a prepended space or joiner.
     */
    fun getDisplayLabel(rawLabel: String): String {
        if (isBengaliCombiningMark(rawLabel) || isStandaloneBengaliCombiningMark(rawLabel)) {
            return "\u00A0$rawLabel"
        }
        return rawLabel
    }

    /**
     * Renders a standalone Bengali combining mark onto the Canvas at (centerX, baselineY)
     * using Path Subtraction (Path.Op.DIFFERENCE) so that the Unicode dotted circle (U+25CC / ◌)
     * forced by HarfBuzz is completely eliminated.
     *
     * 1. Shapes "\u25CC" + mark (combined) and "\u25CC" (baseCircle) as Path outlines at (0, 0).
     * 2. Subtracts the circle path from the combined path using Path.Op.DIFFERENCE.
     * 3. Calculates bounds of the resulting mark geometry.
     * 4. Translates the path so it is centered horizontally at centerX (-bounds.centerX() + centerX)
     *    and aligned vertically with baselineY.
     * 5. Sets Paint.Style.FILL and draws the path onto the Canvas.
     */
    fun drawStandaloneBengaliMark(
        canvas: Canvas,
        mark: String,
        centerX: Float,
        baselineY: Float,
        paint: Paint
    ) {
        val baseCircle = "\u25CC"
        val combined = "$baseCircle$mark"
        val combinedPath = Path()
        val circlePath = Path()

        try {
            paint.getTextPath(combined, 0, combined.length, 0f, 0f, combinedPath)
            paint.getTextPath(baseCircle, 0, baseCircle.length, 0f, 0f, circlePath)

            combinedPath.op(circlePath, Path.Op.DIFFERENCE)

            val bounds = RectF()
            combinedPath.computeBounds(bounds, true)

            if (!combinedPath.isEmpty) {
                val offsetX = -bounds.centerX() + centerX
                val offsetY = baselineY
                combinedPath.offset(offsetX, offsetY)

                val originalStyle = paint.style
                paint.style = Paint.Style.FILL
                try {
                    canvas.drawPath(combinedPath, paint)
                } finally {
                    paint.style = originalStyle
                }
                return
            }
        } catch (_: Throwable) {
            // Safe fallback
        }

        canvas.drawText(mark, centerX, baselineY, paint)
    }

    /**
     * General delegation method for drawing Bengali marks or regular text.
     */
    fun drawCombiningMark(
        canvas: Canvas,
        text: String,
        centerX: Float,
        baselineY: Float,
        paint: Paint
    ) {
        if (isBengaliCombiningMark(text) || isStandaloneBengaliCombiningMark(text)) {
            drawStandaloneBengaliMark(canvas, text, centerX, baselineY, paint)
        } else {
            canvas.drawText(text, centerX, baselineY, paint)
        }
    }
}
