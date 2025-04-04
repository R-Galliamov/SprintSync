package com.developers.sprintsync.domain.track_preview.cropper

import android.graphics.Bitmap
import javax.inject.Inject

class TrackPreviewCropper // TODO Over complication
    @Inject
    constructor(
        private val dimensions: TrackPreviewDimensions,
    ) {
        fun getCroppedBitmap(bitmap: Bitmap) = BitmapCropper.cropToTargetDimensions(bitmap, dimensions.width, dimensions.height)
    }
