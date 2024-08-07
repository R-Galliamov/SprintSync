package com.developers.sprintsync.global.styleProvider.textStyle

interface TextStyleProvider {
    val textColor: Int
    val textSizeDp: Float
    val typeface: android.graphics.Typeface?
}