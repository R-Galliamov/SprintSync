package com.developers.sprintsync.core.components.track_snapshot.data.model

import android.graphics.Bitmap

data class TrackSnapshot(
    val id: Int = 0,
    val trackId: Int,
    val timestamp: Long,
    val bitmap: Bitmap,
)