package com.developers.sprintsync.data.track_preview.repository

import android.util.Log
import com.developers.sprintsync.data.track_preview.model.TrackPreview
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview
import com.developers.sprintsync.data.track_preview.source.database.dao.TrackPreviewDao
import com.developers.sprintsync.data.track_preview.source.database.dto.TrackPreviewEntity
import com.developers.sprintsync.data.track_preview.source.database.dto.toDto
import com.developers.sprintsync.data.track_preview.source.file.LocalTrackPreviewDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface TrackPreviewRepository {
    val tracksWithPreviewsFlow: Flow<List<TrackWithPreview>>

    suspend fun savePreview(preview: TrackPreview)

    suspend fun cleanOrphanFiles()
}

@Singleton
class TrackPreviewRepositoryImpl
    @Inject
    constructor(
        private val dao: TrackPreviewDao,
        private val trackPreviewDataSource: LocalTrackPreviewDataSource,
    ) : TrackPreviewRepository {
        override val tracksWithPreviewsFlow = dao.getAllTracksWithPreviews().map { it.toDto() }

        override suspend fun savePreview(preview: TrackPreview) {
            try {
                val trackPreview = trackPreviewDataSource.saveBitmapToFile(preview)
                val entity = TrackPreviewEntity.fromDto(trackPreview)
                dao.insertPreview(entity)
            } catch (e: Exception) {
                Log.e(TAG, "Fail to save track preview", e)
            }
        }

        override suspend fun cleanOrphanFiles() {
            try {
                val validFilePaths =
                    dao
                        .getAllTrackPreviewPaths()
                        .mapNotNull { it.filePath }
                        .toSet()
                trackPreviewDataSource.cleanOrphanFiles(validFilePaths)
            } catch (e: Exception) {
                Log.e(TAG, "Fail to clean orphan files", e)
            }
        }

        companion object {
            private const val TAG = "My stack: TrackPreviewRepository"
        }
    }
