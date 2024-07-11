package com.developers.sprintsync.tracking.data.repository.useCase

import com.developers.sprintsync.tracking.data.repository.TrackRepository
import javax.inject.Inject

class DeleteTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        suspend operator fun invoke(trackId: Int) = repository.deleteTrackById(trackId)
    }
