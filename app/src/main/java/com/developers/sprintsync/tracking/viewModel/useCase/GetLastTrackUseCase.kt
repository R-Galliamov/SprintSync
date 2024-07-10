package com.developers.sprintsync.tracking.viewModel.useCase

import com.developers.sprintsync.tracking.repository.TrackRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastTrackUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        operator fun invoke() = repository.tracks.map { it.firstOrNull() }
    }
