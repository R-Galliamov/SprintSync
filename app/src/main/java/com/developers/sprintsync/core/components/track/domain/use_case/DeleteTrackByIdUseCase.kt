package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.repository.TrackRepository
import javax.inject.Inject

class DeleteTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        suspend operator fun invoke(trackId: Int) = repository.deleteTrackById(trackId)
    }
