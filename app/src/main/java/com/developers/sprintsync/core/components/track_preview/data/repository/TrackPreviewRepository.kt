package com.developers.sprintsync.core.components.track_preview.data.repository

import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewWrapper
import kotlinx.coroutines.flow.Flow

interface TrackPreviewRepository {
    val trackPreviewWrappersFlow: Flow<List<TrackPreviewWrapper>>

    suspend fun savePreview(bitmap: TrackPreviewBitmap)

    suspend fun cleanOrphanFiles()
}
