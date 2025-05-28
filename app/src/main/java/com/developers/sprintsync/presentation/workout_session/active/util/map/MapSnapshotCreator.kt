package com.developers.sprintsync.presentation.workout_session.active.util.map

import android.graphics.Bitmap
import com.developers.sprintsync.core.util.log.AppLogger
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Creates snapshots of a Google Map for track previews.
 */
@FragmentScoped
class MapSnapshotCreator @Inject constructor(
    private val log: AppLogger
) {
    /**
     * Captures a snapshot of the provided Google Map.
     * @param map The [GoogleMap] to capture.
     * @param onSnapshotReady Callback invoked with the captured [Bitmap] or null if the capture fails.
     */
    fun createSnapshot(
        map: GoogleMap,
        onSnapshotReady: (bitmap: Bitmap?) -> Unit,
    ) {
        map.setOnMapLoadedCallback {
            map.snapshot { bitmap ->
                onSnapshotReady(bitmap)
                if (bitmap != null) {
                    log.i("Map snapshot captured: width=${bitmap.width}, height=${bitmap.height}")
                } else {
                    log.e("Map snapshot failed: bitmap is null")
                }
            }
        }
    }
}
