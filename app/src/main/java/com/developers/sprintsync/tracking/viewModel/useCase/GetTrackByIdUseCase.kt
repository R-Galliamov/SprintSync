package com.developers.sprintsync.tracking.viewModel.useCase

import com.developers.sprintsync.tracking.repository.TrackRepository
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        operator fun invoke(trackId: Int) = repository.getTrackById(trackId)
    }
