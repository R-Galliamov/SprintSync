package com.developers.sprintsync.data.track_preview.source.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.developers.sprintsync.data.track_preview.model.TrackPreview
import com.developers.sprintsync.data.track_preview.source.file.name_generator.TrackPreviewFileNameGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTrackPreviewDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val nameGenerator: TrackPreviewFileNameGenerator,
    ) {

        private val filesDirectory by lazy { File(context.filesDir, FOLDER_NAME) }

        private val dispatcher = Dispatchers.IO

        init {
            ensureFilesDirectoryExists()
        }

        suspend fun saveBitmapToFile(trackPreview: TrackPreview): TrackPreview =
            withContext(dispatcher) {
                val timestamp = System.currentTimeMillis()
                val fileName = nameGenerator.generateName(trackPreview.trackId, timestamp)
                val file = File(filesDirectory, fileName)
                try {
                    FileOutputStream(file).use { outputStream ->
                        trackPreview.bitmap?.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                    }
                    return@withContext trackPreview.copy(bitmapPath = file.path)
                } catch (e: Exception) {
                    throw Exception("Failed to save bitmap to ${file.path}", e)
                }
            }

        suspend fun loadBitmapFromFile(filePath: String): Bitmap =
            withContext(dispatcher) {
                BitmapFactory.decodeFile(filePath) ?: throw IllegalStateException("Failed to load bitmap from $filePath")
            }

        suspend fun cleanOrphanFiles(validFilePaths: Set<String>) {
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

        private fun ensureFilesDirectoryExists() {
            val directory = filesDirectory
            if (!(directory.exists() || directory.mkdir())) {
                Log.w(TAG, "Failed to create track previews directory: $FOLDER_NAME")
            }
        }

        companion object {
            private const val FOLDER_NAME = "track_previews"

            private const val QUALITY = 90

            private const val TAG = "TrackPreviewFileDataSource"
        }
    }
