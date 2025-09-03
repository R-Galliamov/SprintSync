package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track_preview.model.TrackPreview
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import com.developers.sprintsync.domain.core.AppResult
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.storage.SaveTrackUseCase
import com.developers.sprintsync.domain.track.validator.TrackErrors
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
    private val log: AppLogger,
) {
    suspend fun saveTrackWithSnapshot(track: Track): AppResult<Int, TrackErrors> {
        return when (val res = saveTrackUseCase(track)) {
            is AppResult.Success -> {
                val id = res.value

                try {
                    val bitmap = withTimeoutOrNull(SNAPSHOT_REQUEST_TIMEOUT_MS) {
                        snapshotStateHandler.snapshot.first()
                    }
                    if (bitmap != null) {
                        trackPreviewRepository.savePreview(
                            TrackPreview(trackId = id, bitmap = bitmap)
                        )
                    } else {
                        log.w("Snapshot timeout for trackId=$id")
                    }
                } catch (ce: kotlinx.coroutines.CancellationException) {
                    throw ce
                } catch (e: Exception) {
                    log.e("Snapshot capture/save failed for trackId=$id: ${e.message}", e)
                }
                AppResult.Success(id)
            }

            is AppResult.Failure.Validation -> res
            is AppResult.Failure.Unexpected -> res
        }
    }

    private companion object {
        const val SNAPSHOT_REQUEST_TIMEOUT_MS = 10_000L
    }
}
