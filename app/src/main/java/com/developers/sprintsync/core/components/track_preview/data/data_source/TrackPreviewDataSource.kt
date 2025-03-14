package com.developers.sprintsync.core.components.track_preview.data.data_source

import android.graphics.Bitmap
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap

abstract class TrackPreviewDataSource {
    abstract fun saveBitmapToFile(bitmap: TrackPreviewBitmap): String

    abstract fun loadBitmapFromFile(filePath: String): Bitmap
}