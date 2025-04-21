package com.developers.sprintsync.data.track_preview.cropper

import android.graphics.Bitmap
import android.graphics.Matrix

class BitmapRotator {
    companion object {
        private const val INITIAL_X_OFFSET = 0
        private const val INITIAL_Y_OFFSET = 0

        fun rotate(
            source: Bitmap,
            angle: Float,
        ): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            val bitmap =
                Bitmap.createBitmap(
                    source,
                    INITIAL_X_OFFSET,
                    INITIAL_Y_OFFSET,
                    source.width,
                    source.height,
                    matrix,
                    true,
                )
            return bitmap
        }
    }
}
