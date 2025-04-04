package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.SaveTrackUseCase
import com.developers.sprintsync.domain.track_preview.model.TrackPreviewBitmap
import com.developers.sprintsync.domain.track_preview.use_case.SaveTrackPreviewUseCase
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.snapshot.SnapshotStateHandler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TrackCompletionHandler
    @Inject
    constructor(
        private val saveTrackUseCase: SaveTrackUseCase,
        private val saveTrackPreviewUseCase: SaveTrackPreviewUseCase,
        private val snapshotStateHandler: SnapshotStateHandler,
    ) {
        suspend fun saveTrackWithSnapshot(track: Track): Int {
            val trackId = saveTrackUseCase.invoke(track)
            val timestamp = System.currentTimeMillis()
            val snapshotBitmap = withTimeoutOrNull(SNAPSHOT_REQUEST_TIMEOUT_MS) { snapshotStateHandler.snapshot.first() }
            snapshotBitmap?.let { bitmap ->
                val trackPreviewBitmap =
                    TrackPreviewBitmap(
                        trackId = trackId,
                        timestamp = timestamp,
                        bitmap = bitmap,
                    )
                saveTrackPreviewUseCase(trackPreviewBitmap)
            }
            return trackId
        }

        private companion object {
            const val SNAPSHOT_REQUEST_TIMEOUT_MS = 10_000L
        }
    }
