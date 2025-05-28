package com.developers.sprintsync.data.track_preview.util.cropper

import android.graphics.Bitmap
import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Utility for cropping bitmaps to match target dimensions while preserving aspect ratio.
 */
class BitmapCropper @Inject constructor(
    private val bitmapRotator: BitmapRotator,
    private val log: AppLogger,
) {

    companion object {
        private const val NINETY_DEGREES_ROTATION = 90f
        private const val X_OFFSET_DIVISOR = 2
        private const val Y_OFFSET_DIVISOR = 2
        private const val INITIAL_X_OFFSET = 0
        private const val INITIAL_Y_OFFSET = 0
    }


    /**
     * Crops a bitmap to match the target width and height, maintaining the target aspect ratio.
     * Rotates the bitmap if necessary to better fit the target dimensions.
     * @param bitmap The source bitmap to crop.
     * @param targetWidth The desired width of the output bitmap.
     * @param targetHeight The desired height of the output bitmap.
     * @return A cropped bitmap matching the target aspect ratio.
     */
    fun cropToTargetDimensions(
        bitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
    ): Bitmap {
        val aspectRatio = targetWidth.toFloat() / targetHeight.toFloat()
        val currentAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        log.i("Cropping bitmap: original=${bitmap.width}x${bitmap.height}, target=${targetWidth}x${targetHeight}, aspectRatio=$aspectRatio")

        return if (currentAspectRatio > aspectRatio) {
            log.i("Cropping width to match aspect ratio")
            cropWidthToMatchAspectRatio(bitmap, aspectRatio)
        } else {
            log.i("Rotating and cropping height to match aspect ratio")
            val rotatedBitmap = bitmapRotator.rotate(bitmap, NINETY_DEGREES_ROTATION)
            cropHeightToMatchAspectRatio(rotatedBitmap, aspectRatio)
        }
    }

    // Crops the bitmap's width to match the target aspect ratio, centering the crop.
    private fun cropWidthToMatchAspectRatio(
        bitmap: Bitmap,
        aspectRatio: Float,
    ): Bitmap {
        val newWidth = (bitmap.height * aspectRatio).toInt()
        val xOffset = (bitmap.width - newWidth) / X_OFFSET_DIVISOR
        return Bitmap.createBitmap(bitmap, xOffset, INITIAL_Y_OFFSET, newWidth, bitmap.height)
    }

    // Crops the bitmap's height to match the target aspect ratio, centering the crop.
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
