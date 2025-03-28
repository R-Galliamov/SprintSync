package com.developers.sprintsync.core.components.track_preview.data.data_source

import android.graphics.Bitmap
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap

/**
 * Abstract data source for handling track preview images.
 *
 * This class defines the contract for storing, retrieving, and cleaning up track preview images.
 * Implementations should handle file storage and ensure efficient bitmap management.
 */
abstract class TrackPreviewDataSource {

    /**
     * Saves a track preview bitmap to a file.
     *
     * @param previewBitmap The track preview bitmap to be saved.
     * @return The absolute file path of the saved bitmap.
     */
    abstract suspend fun saveBitmapToFile(previewBitmap: TrackPreviewBitmap): String

    /**
     * Loads a track preview bitmap from a file.
     *
     * @param filePath The absolute path of the file containing the bitmap.
     * @return The loaded [Bitmap] instance.
     */
    abstract suspend fun loadBitmapFromFile(filePath: String): Bitmap

    /**
     * Deletes orphaned track preview files that are not part of the valid file paths.
     *
     * This method removes outdated or unnecessary preview images to free up storage.
     *
     * @param validFilePaths A set of valid file paths that should be retained.
     */
    abstract suspend fun cleanOrphanFiles(validFilePaths: Set<String>)
}
