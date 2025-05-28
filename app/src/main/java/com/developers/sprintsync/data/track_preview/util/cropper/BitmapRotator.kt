package com.developers.sprintsync.data.track_preview.util.cropper

import android.graphics.Bitmap
import android.graphics.Matrix
import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Utility for rotating bitmaps by a specified angle.
 */
class BitmapRotator @Inject constructor(
    private val log: AppLogger,
) {
    companion object {
        private const val INITIAL_X_OFFSET = 0
        private const val INITIAL_Y_OFFSET = 0
    }

    /**
     * Rotates a bitmap by the specified angle.
     * @param source The bitmap to rotate.
     * @param angle The rotation angle in degrees.
     * @return The rotated bitmap.
     */
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
        log.i("Bitmap rotated: source=${source.width}x${source.height}, angle=$angle")
        return bitmap
    }
}
