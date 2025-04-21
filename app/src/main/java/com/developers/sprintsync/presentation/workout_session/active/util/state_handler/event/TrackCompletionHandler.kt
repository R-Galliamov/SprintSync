package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.data.track_preview.model.TrackPreview
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.storage.SaveTrackUseCase
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.snapshot.SnapshotStateHandler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TrackCompletionHandler
    @Inject
    constructor(
        private val saveTrackUseCase: SaveTrackUseCase,
        private val snapshotStateHandler: SnapshotStateHandler,
        private val trackPreviewRepository: TrackPreviewRepository,
    ) {
        suspend fun saveTrackWithSnapshot(track: Track): Int {
            val trackId = saveTrackUseCase.invoke(track)
            val snapshotBitmap = withTimeoutOrNull(SNAPSHOT_REQUEST_TIMEOUT_MS) { snapshotStateHandler.snapshot.first() }
            snapshotBitmap?.let { bitmap ->
                val trackPreview =
                    TrackPreview(
                        trackId = trackId,
                        bitmap = bitmap,
                    )
                trackPreviewRepository.savePreview(trackPreview)
            }
            return trackId
        }

        private companion object {
            const val SNAPSHOT_REQUEST_TIMEOUT_MS = 10_000L
        }
    }
