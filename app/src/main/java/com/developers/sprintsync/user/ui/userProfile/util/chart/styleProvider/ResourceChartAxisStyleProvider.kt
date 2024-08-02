package com.developers.sprintsync.user.ui.userProfile.util.chart.styleProvider

import android.content.Context
import androidx.core.content.ContextCompat

class ResourceChartAxisStyleProvider(
    private val context: Context,
    private val styleResId: Int,
) : ChartAxisStyleProvider {
    override val textColor: Int by lazy {
        val typedArray = context.obtainStyledAttributes(styleResId, intArrayOf(android.R.attr.textColor))
        val color = typedArray.getColor(INDEX_TEXT_COLOR, ContextCompat.getColor(context, android.R.color.black))
        typedArray.recycle()
        color
    }

    override val typeface: android.graphics.Typeface? by lazy {
        val typedArray = context.obtainStyledAttributes(styleResId, intArrayOf(android.R.attr.textStyle))
        val textStyle = typedArray.getInt(INDEX_TEXT_STYLE, android.graphics.Typeface.NORMAL)
        typedArray.recycle()

        when (textStyle) {
            android.graphics.Typeface.BOLD -> android.graphics.Typeface.DEFAULT_BOLD
            android.graphics.Typeface.ITALIC ->
                android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.ITALIC,
                )

            android.graphics.Typeface.BOLD_ITALIC ->
                android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD_ITALIC,
                )

            else -> null
        }
    }

    companion object {
        private const val INDEX_TEXT_COLOR = 0
        private const val INDEX_TEXT_STYLE = 0
    }
}
