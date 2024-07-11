package com.developers.sprintsync.tracking.dataStorage.repository.useCase

import com.developers.sprintsync.tracking.dataStorage.repository.TrackRepository
import com.developers.sprintsync.tracking.session.model.track.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTracksUseCase
    @Inject
    constructor(
        repository: TrackRepository,
    ) {
        val tracks: Flow<List<Track>> = repository.tracks
    }
