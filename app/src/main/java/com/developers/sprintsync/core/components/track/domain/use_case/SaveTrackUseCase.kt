package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.repository.TrackRepository
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class SaveTrackUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        suspend operator fun invoke(track: Track) {
            repository.saveTrack(track)
        }
    }
