package com.developers.sprintsync.presentation.workout_session.active.util.map

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
