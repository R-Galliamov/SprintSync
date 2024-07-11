package com.developers.sprintsync.tracking.data.repository.useCase

import com.developers.sprintsync.tracking.data.repository.TrackRepository
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        operator fun invoke(trackId: Int) = repository.getTrackById(trackId)
    }
