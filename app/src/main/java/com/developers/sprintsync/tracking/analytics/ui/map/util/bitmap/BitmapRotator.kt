package com.developers.sprintsync.tracking.analytics.ui.map.util.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix

class BitmapRotator {
    companion object {
        fun rotate(
            source: Bitmap,
            angle: Float,
        ): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }
}
