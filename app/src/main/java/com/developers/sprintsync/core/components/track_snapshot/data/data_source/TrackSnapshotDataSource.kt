package com.developers.sprintsync.core.components.track_snapshot.data.data_source

import android.graphics.Bitmap
import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot

abstract class TrackSnapshotDataSource {
    abstract fun saveBitmapToFile(snapshot: TrackSnapshot): Result<String>

    abstract fun loadBitmapFromFile(filePath: String): Result<Bitmap>
}