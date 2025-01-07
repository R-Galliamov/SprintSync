package com.developers.sprintsync.core.util.style_provider.textStyle

interface TextStyleProvider {
    val textColor: Int
    val textSizeDp: Float
    val typeface: android.graphics.Typeface?
}