package io.lipika.common.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Region
import android.os.Build
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan

/**
 * Utility for detecting standalone Bengali combining marks (dependent vowel signs / Kars,
 * Hasanta, and diacritics) and rendering them on Android Canvas without the unwanted
 * Unicode dotted circle (◌ / U+25CC) injected by HarfBuzz/Bidi text shapers.
 */
object BengaliMatraUtils {

    /**
     * Identifies if a character is a Bengali dependent vowel sign (Kar/Matra), Hasanta, or diacritic:
     * - \u0981 (ঁ Chandrabindu)
     * - \u0982 (ং Anusvara)
     * - \u0983 (ঃ Visarga)
     * - \u09BC (় Nukta)
     * - \u09BE to \u09CD (া, ি, ী, ু, ূ, ৃ, ৄ, ে, ৈ, ো, ৌ, ্)
     * - \u09D7 (ৗ Au length mark)
     * - \u09E2 to \u09E3 (ৢ, ৣ)
     */
    fun isBengaliCombiningChar(c: Char): Boolean {
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
        val clean = str.filter { it != '\u200C' && it != '\u200D' && it != ' ' && it != '\u00A0' && it != '\u25CC' }
        return clean.isNotEmpty() && clean.all { isBengaliCombiningChar(it) }
    }

    /**
     * Prepares a string for Canvas / Text rendering with a prepended space or joiner.
     */
    fun getDisplayLabel(rawLabel: String): String {
        if (isStandaloneBengaliCombiningMark(rawLabel)) {
            return "\u00A0$rawLabel"
        }
        return rawLabel
    }

    /**
     * Renders a standalone Bengali combining mark (or any label) onto the Canvas at (centerX, baselineY)
     * completely eliminating the unwanted dotted circle (◌ / U+25CC) and ensuring the Matra remains
     * properly centered on the keycap.
     *
     * Implementation:
     * 1. If TextPaint is available, renders via a transparent base (\u25CC) with ForegroundColorSpan,
     *    which allows HarfBuzz to shape the mark with full glyph geometry without drawing the dotted circle.
     * 2. In addition, clips/masks out any U+25CC dotted circle bounding area so that even if HarfBuzz
     *    forces U+25CC onto the Canvas, the dotted circle is never drawn.
     */
    fun drawCombiningMark(
        canvas: Canvas,
        text: String,
        centerX: Float,
        baselineY: Float,
        paint: Paint
    ) {
        if (!isStandaloneBengaliCombiningMark(text)) {
            canvas.drawText(text, centerX, baselineY, paint)
            return
        }

        // Try transparent base layout method first for perfect HarfBuzz cluster rendering
        if (paint is TextPaint) {
            try {
                drawWithTransparentBase(canvas, text, centerX, baselineY, paint)
                return
            } catch (_: Throwable) {
                // Fall back to space-offset & clip method
            }
        }

        drawWithSpaceOffsetAndClip(canvas, text, centerX, baselineY, paint)
    }

    /**
     * Method A: Prepend Space (\u0020), offset X by space width, and clip out the dotted circle
     * bounding box so only the standalone mark is drawn and centered on the key.
     */
    fun drawWithSpaceOffsetAndClip(
        canvas: Canvas,
        text: String,
        centerX: Float,
        baselineY: Float,
        paint: Paint
    ) {
        val originalAlign = paint.textAlign
        paint.textAlign = Paint.Align.LEFT
        try {
            val spaceStr = "\u0020"
            val spaceWidth = paint.measureText(spaceStr)
            val combined = "$spaceStr$text"
            val totalWidth = paint.measureText(combined)
            val markWidth = (totalWidth - spaceWidth).coerceAtLeast(0f)

            // Center the mark itself at centerX
            val drawX = centerX - spaceWidth - (markWidth / 2f)

            // Dotted circle box to mask out
            val circleWidth = paint.measureText("\u25CC").coerceAtLeast(spaceWidth)
            val fontMetrics = paint.fontMetrics
            val circleTop = baselineY + fontMetrics.ascent
            val circleBottom = baselineY + fontMetrics.descent

            val saveCount = canvas.save()
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canvas.clipOutRect(drawX, circleTop, drawX + circleWidth, circleBottom)
                } else {
                    @Suppress("DEPRECATION")
                    canvas.clipRect(drawX, circleTop, drawX + circleWidth, circleBottom, Region.Op.DIFFERENCE)
                }
                canvas.drawText(combined, drawX, baselineY, paint)
            } catch (_: Throwable) {
                canvas.drawText(combined, drawX, baselineY, paint)
            } finally {
                canvas.restoreToCount(saveCount)
            }
        } finally {
            paint.textAlign = originalAlign
        }
    }

    /**
     * Method B: Renders using a SpannableString with a transparent base (\u25CC).
     * HarfBuzz shapes the mark onto \u25CC, but \u25CC has Color.TRANSPARENT,
     * so 100% of the dotted circle is invisible while the mark retains its exact glyph shape.
     */
    fun drawWithTransparentBase(
        canvas: Canvas,
        text: String,
        centerX: Float,
        baselineY: Float,
        textPaint: TextPaint
    ) {
        val baseChar = "\u25CC"
        val fullStr = "$baseChar$text"
        val originalColor = textPaint.color
        val spannable = SpannableString(fullStr).apply {
            setSpan(
                ForegroundColorSpan(Color.TRANSPARENT),
                0, 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(originalColor),
                1, fullStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val totalWidth = textPaint.measureText(fullStr)
        val baseWidth = textPaint.measureText(baseChar)
        val layoutWidth = (totalWidth.toInt() + 32).coerceAtLeast(1)

        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(spannable, 0, spannable.length, textPaint, layoutWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(spannable, textPaint, layoutWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }

        val markWidth = (totalWidth - baseWidth).coerceAtLeast(0f)

        // Center the mark itself at centerX
        val layoutLeft = centerX - (baseWidth / 2f) - (markWidth / 2f)
        val layoutTop = baselineY + textPaint.fontMetrics.ascent

        val saveCount = canvas.save()
        try {
            canvas.translate(layoutLeft, layoutTop)
            staticLayout.draw(canvas)
        } finally {
            canvas.restoreToCount(saveCount)
        }
    }
}
