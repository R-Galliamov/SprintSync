package com.developers.sprintsync.tracking.data.repository.useCase

import com.developers.sprintsync.tracking.data.repository.TrackRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastTrackUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        operator fun invoke() = repository.tracks.map { it.firstOrNull() }
    }
