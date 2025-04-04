package com.developers.sprintsync.domain.track_preview.model

import android.graphics.Bitmap

data class TrackPreviewBitmap(
    val id: Int = 0,
    val trackId: Int,
    val timestamp: Long,
    val bitmap: Bitmap,
)