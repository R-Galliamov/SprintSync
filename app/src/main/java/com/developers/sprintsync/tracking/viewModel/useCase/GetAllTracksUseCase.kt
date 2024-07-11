package com.developers.sprintsync.tracking.viewModel.useCase

import com.developers.sprintsync.tracking.model.track.Track
import com.developers.sprintsync.tracking.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTracksUseCase
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) {
        val tracks: Flow<List<Track>> = repository.tracks
    }
