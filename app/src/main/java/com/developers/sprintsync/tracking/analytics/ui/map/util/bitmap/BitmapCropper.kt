package com.developers.sprintsync.tracking.analytics.ui.map.util.bitmap

import android.graphics.Bitmap

class BitmapCropper {
    companion object {
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
                val rotatedBitmap = BitmapRotator.rotate(bitmap, 90f)
                cropHeightToMatchAspectRatio(rotatedBitmap, aspectRatio)
            }
        }

        private fun cropWidthToMatchAspectRatio(
            bitmap: Bitmap,
            aspectRatio: Float,
        ): Bitmap {
            val newWidth = (bitmap.height * aspectRatio).toInt()
            val xOffset = (bitmap.width - newWidth) / 2
            return Bitmap.createBitmap(bitmap, xOffset, 0, newWidth, bitmap.height)
        }

        private fun cropHeightToMatchAspectRatio(
            bitmap: Bitmap,
            aspectRatio: Float,
        ): Bitmap {
            val newHeight = (bitmap.width / aspectRatio).toInt()
            val yOffset =
                maxOf(0, (bitmap.height - newHeight) / 2)
            return Bitmap.createBitmap(bitmap, 0, yOffset, bitmap.width, newHeight)
        }
    }
}
