package com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.event

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.domain.use_case.SaveTrackUseCase
import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot
import com.developers.sprintsync.core.components.track_snapshot.domain.use_case.SaveSnapshotUseCase
import com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.snapshot.SnapshotStateHandler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TrackCompletionHandler
    @Inject
    constructor(
        private val saveTrackUseCase: SaveTrackUseCase,
        private val saveSnapshotUseCase: SaveSnapshotUseCase,
        private val snapshotStateHandler: SnapshotStateHandler,
    ) {
        suspend fun saveTrackWithSnapshot(track: Track): Int {
            val trackId = saveTrackUseCase.invoke(track)
            val timestamp = System.currentTimeMillis()
            val snapshotBitmap = withTimeoutOrNull(SNAPSHOT_REQUEST_TIMEOUT_MS) { snapshotStateHandler.snapshot.first() }
            snapshotBitmap?.let { bitmap ->
                val trackSnapshot =
                    TrackSnapshot(
                        trackId = trackId,
                        timestamp = timestamp,
                        bitmap = bitmap,
                    )
                saveSnapshotUseCase(trackSnapshot)
            }
            return trackId
        }

        private companion object {
            const val SNAPSHOT_REQUEST_TIMEOUT_MS = 10_000L
        }
    }
