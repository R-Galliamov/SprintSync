package com.developers.sprintsync.data.track_preview.repository

import com.developers.sprintsync.core.util.log.AppLogger
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

/**
 * Repository for managing track preview data and associated image files.
 */
interface TrackPreviewRepository {
    val tracksWithPreviewsFlow: Flow<List<TrackWithPreview>>

    suspend fun savePreview(preview: TrackPreview)
}

/**
 * Implementation of [TrackPreviewRepository] for handling track previews with database and file storage.
 */
@Singleton
class LocalFileDbTrackPreviewRepository
@Inject
constructor(
    private val dao: TrackPreviewDao,
    private val trackPreviewDataSource: LocalTrackPreviewDataSource,
    private val log: AppLogger,
) : TrackPreviewRepository {

    /**
     * Flow of all tracks with their previews, mapped from database entities to domain models.
     */
    override val tracksWithPreviewsFlow = dao.getAllTracksWithPreviews().map { it.toDto() }

    /**
     * Saves a track preview by storing its bitmap to a file and updating the database.
     * @param preview The track preview to save.
     */
    override suspend fun savePreview(preview: TrackPreview) {
        try {
            val trackPreview = trackPreviewDataSource.saveBitmapToFile(preview)
            val entity = TrackPreviewEntity.fromDto(trackPreview)
            dao.insertPreview(entity)
            log.i("Track preview saved: $trackPreview")
        } catch (e: Exception) {
            log.e("Failed to save track preview: ${e.message}", e)
        }
    }

    /**
     * Removes orphaned preview files not referenced in the database.
     */
    suspend fun cleanOrphanFiles() {
        try {
            val validFilePaths =
                dao.getAllTrackPreviewPaths()
                    .mapNotNull { it.filePath }
                    .toSet()
            val deletedAmount = trackPreviewDataSource.cleanOrphanFiles(validFilePaths)
            log.i("$deletedAmount orphan files were cleaned")
        } catch (e: Exception) {
            log.e("Failed to clean orphan files: ${e.message}", e)
        }
    }
}
