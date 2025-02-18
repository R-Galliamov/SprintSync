package com.developers.sprintsync.core.components.track_snapshot.presentation.util

import android.graphics.Bitmap
import com.developers.sprintsync.core.components.track_snapshot.presentation.model.SnapshotDimensions
import com.developers.sprintsync.core.util.bitmap.BitmapCropper
import javax.inject.Inject

class TrackSnapshotCropper
    @Inject
    constructor(
        private val dimensions: SnapshotDimensions,
    ) {
        fun getCroppedSnapshot(snapshot: Bitmap) = BitmapCropper.cropToTargetDimensions(snapshot, dimensions.width, dimensions.height)
    }
