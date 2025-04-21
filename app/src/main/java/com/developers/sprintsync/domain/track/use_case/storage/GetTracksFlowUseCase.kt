package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksFlowUseCase
    @Inject
    constructor(
        trackRepository: TrackRepository,
    ) {
        val tracks: Flow<List<Track>> = trackRepository.tracks
    }
