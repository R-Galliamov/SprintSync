package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.core.application.App
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.core.AppResult
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.validator.TrackErrors
import com.developers.sprintsync.domain.track.validator.TrackValidator

/**
 * Use case for saving a track to the repository after validation and preparation.
 */
class SaveTrackUseCase(
    private val validator: TrackValidator,
    private val repo: TrackRepository,
    private val log: AppLogger,
) {
    suspend operator fun invoke(track: Track): AppResult<Int, TrackErrors> {
        val errors = validator.validate(track)
        if (errors.isNotEmpty()) {
            log.w("Validation failed: $errors")
            return AppResult.Failure.Validation(errors)
        }

        return try {
            val id = repo.save(track)
            log.i("Track saved: id=$id")
            AppResult.Success(id)
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            log.e("Error saving track: ${e.message}", e)
            AppResult.Failure.Unexpected(e)
        }
    }
}

