package com.developers.sprintsync.data.track_preview.data_source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.developers.sprintsync.data.track_preview.data_source.util.name_generator.TrackPreviewFileNameGenerator
import com.developers.sprintsync.domain.track_preview.model.TrackPreviewBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A data source responsible for saving, loading, and managing track preview bitmap files.
 *
 * This class handles file operations such as storing bitmaps on disk, retrieving them,
 * and cleaning up orphaned files. It ensures that all file operations are executed
 * in the IO dispatcher to prevent blocking the main thread.
 *
 * @property context The application context.
 * @property nameGenerator Generates unique file names for track preview images.
 */
@Singleton
class TrackPreviewFileDataSource
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val nameGenerator: TrackPreviewFileNameGenerator,
) : TrackPreviewDataSource() {

    // Directory where track preview images will be stored
    private val filesDirectory by lazy { File(context.filesDir, FOLDER_NAME) }

    // Coroutine dispatcher for performing file I/O operations asynchronously
    private val dispatcher = Dispatchers.IO

    init {
        ensureFilesDirectoryExists()
    }

    /**
     * Saves a bitmap image associated with a track preview to a file.
     *
     * @param previewBitmap The track preview bitmap to save.
     * @return The absolute file path of the saved bitmap.
     * @throws IOException If the file cannot be written.
     */
    override suspend fun saveBitmapToFile(previewBitmap: TrackPreviewBitmap): String =
        withContext(dispatcher) {
            val fileName = nameGenerator.generateName(previewBitmap.trackId, previewBitmap.timestamp)
            val file = File(filesDirectory, fileName)
            try {
                FileOutputStream(file).use { outputStream ->
                    previewBitmap.bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                }
                file.path
            } catch (e: IOException) {
                throw IOException("Failed to save bitmap to ${file.path}", e)
            }
        }

    /**
     * Loads a bitmap from a file.
     *
     * @param filePath The absolute path of the file to load.
     * @return The loaded [Bitmap] object.
     * @throws IllegalStateException If the bitmap cannot be loaded.
     */
    override suspend fun loadBitmapFromFile(filePath: String): Bitmap =
        withContext(dispatcher) {
            BitmapFactory.decodeFile(filePath) ?: throw IllegalStateException("Failed to load bitmap from $filePath")
        }

    /**
     * Deletes orphaned files that are not included in the valid file paths set.
     *
     * @param validFilePaths A set of file paths that should be retained. Any other file in
     *        the directory will be considered orphaned and deleted.
     */
    override suspend fun cleanOrphanFiles(validFilePaths: Set<String>) {
        withContext(dispatcher) {
            filesDirectory.listFiles()?.forEach { file ->
                if (file.absolutePath !in validFilePaths) {
                    if (!file.delete()) {
                        Log.w(TAG, "Failed to delete file: ${file.path}")
                    }
                }
            }
        }
    }

    /**
     * Ensures that the directory for storing track preview files exists.
     * If it does not exist, an attempt is made to create it.
     */
    private fun ensureFilesDirectoryExists() {
        val directory = filesDirectory
        if (!(directory.exists() || directory.mkdir())) {
            Log.w(TAG, "Failed to create track previews directory: $FOLDER_NAME")
        }
    }

    companion object {
        /** Name of the folder where track previews will be stored. */
        private const val FOLDER_NAME = "track_previews"

        /** Compression quality for JPEG images (0-100). */
        private const val QUALITY = 90

        /** Log tag for debugging purposes. */
        private const val TAG = "TrackPreviewFileDataSource"
    }
}
