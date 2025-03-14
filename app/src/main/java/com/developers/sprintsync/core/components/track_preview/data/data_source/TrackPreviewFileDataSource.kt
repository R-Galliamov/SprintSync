package com.developers.sprintsync.core.components.track_preview.data.data_source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.developers.sprintsync.core.components.track_preview.data.data_source.util.name_generator.TrackPreviewFileNameGenerator
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackPreviewFileDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val nameGenerator: TrackPreviewFileNameGenerator,
    ) : TrackPreviewDataSource() {
        override fun saveBitmapToFile(bitmap: TrackPreviewBitmap): String {
            val fileName = nameGenerator.generateName(bitmap.trackId, bitmap.timestamp)
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            return file.path
        }

        override fun loadBitmapFromFile(filePath: String): Bitmap = BitmapFactory.decodeFile(filePath)
    }
