package com.developers.sprintsync.core.components.track_preview.presentation.util

import android.graphics.Bitmap
import com.developers.sprintsync.core.components.track_preview.presentation.model.TrackPreviewDimensions
import com.developers.sprintsync.core.util.bitmap.BitmapCropper
import javax.inject.Inject

class TrackPreviewCropper
    @Inject
    constructor(
        private val dimensions: TrackPreviewDimensions,
    ) {
        fun getCroppedBitmap(bitmap: Bitmap) = BitmapCropper.cropToTargetDimensions(bitmap, dimensions.width, dimensions.height)
    }
