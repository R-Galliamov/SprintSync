package com.developers.sprintsync.tracking.dataStorage.repository.track.useCase

import com.developers.sprintsync.tracking.dataStorage.repository.track.TrackRepository
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        operator fun invoke(trackId: Int) = repository.getTrackById(trackId)
    }
