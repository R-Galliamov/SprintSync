package com.developers.sprintsync.core.components.track_preview.data.repository

import com.developers.sprintsync.core.components.track_preview.data.data_source.TrackPreviewDataSource
import com.developers.sprintsync.core.components.track_preview.data.database.dao.TrackPreviewDao
import com.developers.sprintsync.core.components.track_preview.data.database.dto.TrackPreviewPathEntity
import com.developers.sprintsync.core.components.track_preview.data.database.dto.toDto
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewPath
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackPreviewRepositoryImpl
    @Inject
    constructor(
        private val dao: TrackPreviewDao,
        private val trackPreviewDataSource: TrackPreviewDataSource,
    ) : TrackPreviewRepository {
        override val trackPreviewWrappersFlow = dao.getAllTrackPreviewWrappers().map { it.toDto() }

        override suspend fun savePreview(bitmap: TrackPreviewBitmap) {
            val path = trackPreviewDataSource.saveBitmapToFile(bitmap)
            val trackPreviewPath = TrackPreviewPath.fromBitmap(bitmap, path)
            val entity = TrackPreviewPathEntity.fromDto(trackPreviewPath)
            dao.insertPreview(entity)
        }

        override suspend fun cleanOrphanFiles() {
            val validFilePaths =
                dao
                    .getAllTrackPreviewPaths()
                    .mapNotNull { it.filePath }
                    .toSet()
            trackPreviewDataSource.cleanOrphanFiles(validFilePaths)
        }

        companion object {
            private const val TAG = "My stack: TrackPreviewRepository"
        }
    }
