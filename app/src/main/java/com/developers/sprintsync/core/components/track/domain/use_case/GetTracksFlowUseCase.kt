package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.repository.TrackRepository
import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksFlowUseCase
    @Inject
    constructor(
        repository: TrackRepository,
    ) {
        val tracks: Flow<List<Track>> = repository.tracks
    }
