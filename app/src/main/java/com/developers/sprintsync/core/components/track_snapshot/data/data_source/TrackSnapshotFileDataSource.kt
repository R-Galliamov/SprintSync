package com.developers.sprintsync.core.components.track_snapshot.data.data_source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.developers.sprintsync.core.components.track_snapshot.data.data_source.util.name_generator.SnapshotFileNameGenerator
import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackSnapshotFileDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val nameGenerator: SnapshotFileNameGenerator,
    ) : TrackSnapshotDataSource() {
        override fun saveBitmapToFile(snapshot: TrackSnapshot): Result<String> {
            val fileName = nameGenerator.generateName(snapshot.trackId, snapshot.timestamp)
            val file = File(context.filesDir, fileName)
            return kotlin.runCatching {
                FileOutputStream(file).use { outputStream ->
                    snapshot.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
                file.path
            }
        }

        override fun loadBitmapFromFile(filePath: String): Result<Bitmap> =
            kotlin.runCatching {
                BitmapFactory.decodeFile(filePath)
            }
    }
