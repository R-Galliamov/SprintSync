package com.developers.sprintsync.data.track_preview.repository

import com.developers.sprintsync.domain.track_preview.model.TrackPreviewBitmap
import com.developers.sprintsync.domain.track_preview.model.TrackPreviewWrapper
import kotlinx.coroutines.flow.Flow

interface TrackPreviewRepository {
    val trackPreviewWrappersFlow: Flow<List<TrackPreviewWrapper>>

    suspend fun savePreview(bitmap: TrackPreviewBitmap)

    suspend fun cleanOrphanFiles()
}
