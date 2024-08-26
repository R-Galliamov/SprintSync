package com.developers.sprintsync.tracking.dataStorage.repository.track.useCase

import com.developers.sprintsync.tracking.dataStorage.repository.track.TrackRepository
import com.developers.sprintsync.tracking.session.model.track.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksFlowUseCase
    @Inject
    constructor(
        repository: TrackRepository,
    ) {
        val tracks: Flow<List<Track>> = repository.tracks
    }
