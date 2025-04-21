package com.developers.sprintsync.data.track_preview.model

import android.graphics.Bitmap

data class TrackPreview(
    val id: Int = 0,
    val trackId: Int,
    val bitmap: Bitmap? = null,
    val bitmapPath: String? = null,
)
