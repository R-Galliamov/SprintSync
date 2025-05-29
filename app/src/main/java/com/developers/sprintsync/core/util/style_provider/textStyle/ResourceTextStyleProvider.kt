package com.developers.sprintsync.core.util.style_provider.textStyle

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.developers.sprintsync.core.util.log.TimberAppLogger

/**
 * Interface defining text styling attributes.
 */
interface TextStyleProvider {
    val textColor: Int
    val textSizeDp: Float
    val typeface: Typeface?
}

/**
 * Provides text styling attributes from a style resource.
 * TODO use DaggerHilt
 */
class ResourceTextStyleProvider(
    private val context: Context,
    private val styleResId: Int,
) : TextStyleProvider {

    private val log = TimberAppLogger() // TODO inject this

    /**
     * The text color from the style resource, defaulting to black.
     */
    override val textColor: Int by lazy {
        try {
            obtainStyledAttributes(android.R.attr.textColor).use { attributes ->
                val color = attributes.getColor(INDEX, ContextCompat.getColor(context, android.R.color.black))
                log.d("Text color loaded: $color")
                color
            }
        } catch (e: Exception) {
            log.e("Error loading text color: ${e.message}", e)
            ContextCompat.getColor(context, android.R.color.black)
        }
    }

    /**
     * The text size in DP from the style resource, defaulting to 12px converted to DP.
     */
    override val textSizeDp: Float by lazy {
        try {
            obtainStyledAttributes(android.R.attr.textSize).use { attributes ->
                val size = attributes.getDimension(INDEX, DEFAULT_TEXT_SIZE_PX).pxToDp()
                log.d("Text size loaded: $size dp")
                size
            }
        } catch (e: Exception) {
            log.e("Error loading text size: ${e.message}", e)
            DEFAULT_TEXT_SIZE_PX.pxToDp()
        }
    }

    /**
     * The typeface from the style resource, if defined.
     */
    override val typeface: Typeface? by lazy {
        try {
            obtainStyledAttributes(android.R.attr.textStyle).use { attributes ->
                val textStyle = attributes.getInt(INDEX, Typeface.NORMAL)
                val typeface = when (textStyle) {
                    Typeface.BOLD -> Typeface.DEFAULT_BOLD
                    Typeface.ITALIC -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    Typeface.BOLD_ITALIC -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                    else -> null
                }
                log.d("Typeface loaded: ${typeface?.style ?: "none"}")
                typeface
            }
        } catch (e: Exception) {
            log.e("Error loading typeface: ${e.message}", e)
            null
        }
    }

    // Retrieves styled attributes for a given element
    private fun obtainStyledAttributes(element: Int) = context.obtainStyledAttributes(styleResId, intArrayOf(element))

    // Converts pixels to density-independent pixels
    private fun Float.pxToDp() = this / context.resources.displayMetrics.density

    companion object {
        private const val DEFAULT_TEXT_SIZE_PX = 12f
        private const val INDEX = 0
    }
}