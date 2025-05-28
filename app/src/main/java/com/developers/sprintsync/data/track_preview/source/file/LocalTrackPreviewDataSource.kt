package com.developers.sprintsync.data.track_preview.source.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track_preview.model.TrackPreview
import com.developers.sprintsync.data.track_preview.source.file.name_generator.TrackPreviewFileNameGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for managing track preview bitmaps stored as local files.
 */
@Singleton
class LocalTrackPreviewDataSource
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val nameGenerator: TrackPreviewFileNameGenerator,
    private val log: AppLogger,
) {

    private val filesDirectory by lazy { File(context.filesDir, FOLDER_NAME) }

    private val dispatcher = Dispatchers.IO

    init {
        ensureFilesDirectoryExists()
    }

    /**
     * Saves a track preview's bitmap to a file and returns an updated [TrackPreview] with the file path.
     * @param trackPreview The track preview containing the bitmap to save.
     * @return The updated [TrackPreview] with the saved file path.
     * @throws Exception if the bitmap cannot be saved to the file.
     */
    suspend fun saveBitmapToFile(trackPreview: TrackPreview): TrackPreview =
        withContext(dispatcher) {
            val timestamp = System.currentTimeMillis()
            val fileName = nameGenerator.generateName(trackPreview.trackId, timestamp)
            val file = File(filesDirectory, fileName)
            try {
                FileOutputStream(file).use { outputStream ->
                    trackPreview.bitmap?.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                }
                log.i("Bitmap saved to file: ${file.path}")
                return@withContext trackPreview.copy(bitmapPath = file.path)
            } catch (e: Exception) {
                log.e( "Failed to save bitmap to ${file.path}: ${e.message}", e)
                throw Exception("Failed to save bitmap to ${file.path}", e)
            }
        }

    /**
     * Deletes orphaned preview files not included in the provided valid file paths.
     * @param validFilePaths Set of file paths that should be retained.
     * @return The number of orphaned files deleted.
     */
    suspend fun cleanOrphanFiles(validFilePaths: Set<String>): Int = withContext(dispatcher) {
        val files = filesDirectory.listFiles() ?: return@withContext 0

        return@withContext files.count { file ->
            val isOrphan = file.absolutePath !in validFilePaths
            if (isOrphan) {
                val deleted = file.delete()
                if (!deleted) {
                    log.w("Failed to delete file: ${file.path}")
                }
                deleted
            } else {
                false
            }
        }
    }

    private fun ensureFilesDirectoryExists() {
        val directory = filesDirectory
        if (!(directory.exists() || directory.mkdir())) {
            log.w("Failed to create track previews directory: $FOLDER_NAME")
        }
    }

    /*
   /**
    * Loads a bitmap from the specified file path.
    * @param filePath The path to the bitmap file.
    * @return The loaded [Bitmap].
    * @throws IllegalStateException if the bitmap cannot be loaded.
    */
   suspend fun loadBitmapFromFile(filePath: String): Bitmap =
       withContext(dispatcher) {
           try {
               BitmapFactory.decodeFile(filePath)
                   ?: throw IllegalStateException("Failed to load bitmap from $filePath")
           } catch (e: Exception) {
               log.e( e, "Failed to load bitmap from $filePath: ${e.message}")
               throw e
           }
       }

    */

    companion object {
        private const val FOLDER_NAME = "track_previews"
        private const val QUALITY = 90
    }
}
