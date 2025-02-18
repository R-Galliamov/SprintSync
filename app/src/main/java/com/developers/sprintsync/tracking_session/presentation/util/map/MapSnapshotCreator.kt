package com.developers.sprintsync.tracking_session.presentation.util.map

import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap

object MapSnapshotCreator {
    fun createSnapshot(
        map: GoogleMap,
        onSnapshotReady: (bitmap: Bitmap?) -> Unit,
    ) {
        map.setOnMapLoadedCallback {
            map.snapshot { bitmap ->
                onSnapshotReady(bitmap)
            }
        }
    }
}
