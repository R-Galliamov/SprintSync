package com.developers.sprintsync.tracking.analytics.ui.map.util.bitmap

import android.graphics.Bitmap

class BitmapCropper {
    companion object {
        private const val NINETY_DEGREES_ROTATION = 90f
        private const val X_OFFSET_DIVISOR = 2
        private const val Y_OFFSET_DIVISOR = 2
        private const val INITIAL_X_OFFSET = 0
        private const val INITIAL_Y_OFFSET = 0

        fun cropToTargetDimensions(
            bitmap: Bitmap,
            targetWidth: Int,
            targetHeight: Int,
        ): Bitmap {
            val aspectRatio = targetWidth.toFloat() / targetHeight.toFloat()
            val currentAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

            return if (currentAspectRatio > aspectRatio) {
                cropWidthToMatchAspectRatio(bitmap, aspectRatio)
            } else {
                val rotatedBitmap = BitmapRotator.rotate(bitmap, NINETY_DEGREES_ROTATION)
                cropHeightToMatchAspectRatio(rotatedBitmap, aspectRatio)
            }
        }

        private fun cropWidthToMatchAspectRatio(
            bitmap: Bitmap,
            aspectRatio: Float,
        ): Bitmap {
            val newWidth = (bitmap.height * aspectRatio).toInt()
            val xOffset = (bitmap.width - newWidth) / X_OFFSET_DIVISOR
            return Bitmap.createBitmap(bitmap, xOffset, INITIAL_Y_OFFSET, newWidth, bitmap.height)
        }

        private fun cropHeightToMatchAspectRatio(
            bitmap: Bitmap,
            aspectRatio: Float,
        ): Bitmap {
            val newHeight = (bitmap.width / aspectRatio).toInt()
            val yOffset =
                maxOf(INITIAL_Y_OFFSET, (bitmap.height - newHeight) / Y_OFFSET_DIVISOR)
            return Bitmap.createBitmap(bitmap, INITIAL_X_OFFSET, yOffset, bitmap.width, newHeight)
        }
    }
}
