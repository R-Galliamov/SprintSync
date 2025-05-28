package com.developers.sprintsync.data.track_preview.source.file.name_generator

import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Generates file names for track preview snapshots based on track ID and timestamp.
 */
class TrackPreviewFileNameGenerator
@Inject
constructor(
    private val log: AppLogger,
) {

    /**
     * Generates a unique file name for a track preview snapshot.
     * @param trackId The ID of the associated track.
     * @param timestamp The timestamp of the snapshot in milliseconds.
     * @return A file name in the format "snapshot_[trackId]_[timestamp].jpg".
     */
    fun generateName(
        trackId: Int,
        timestamp: Long,
    ): String {
        val fileName = FILE_NAME_TEMPLATE.format(trackId, timestamp)
        log.i("Generated file name: $fileName for trackId=$trackId, timestamp=$timestamp")
        return fileName
    }

    companion object {
        private const val FILE_NAME_TEMPLATE = "snapshot_%d_%d.jpg"
    }
}
