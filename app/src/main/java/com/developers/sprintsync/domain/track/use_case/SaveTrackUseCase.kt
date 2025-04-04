package com.developers.sprintsync.domain.track.use_case

import com.developers.sprintsync.data.track.data_source.TrackDataSource
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.validation.track.TrackValidator
import javax.inject.Inject

class SaveTrackUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        suspend operator fun invoke(track: Track): Int {
            TrackValidator.validateOrThrow(track)
            return repository.saveTrack(track)
        }
    }
