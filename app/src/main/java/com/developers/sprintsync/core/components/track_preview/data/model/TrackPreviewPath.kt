package com.developers.sprintsync.core.components.track_preview.data.model

data class TrackPreviewPath(
    val id: Int,
    val trackId: Int,
    val timestamp: Long,
    val filePath: String,
) {
    companion object {
        fun fromBitmap(
            bitmap: TrackPreviewBitmap,
            filePath: String,
        ): TrackPreviewPath =
            TrackPreviewPath(
                id = bitmap.id,
                trackId = bitmap.trackId,
                timestamp = bitmap.timestamp,
                filePath = filePath,
            )
    }
}
