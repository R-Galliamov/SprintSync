package com.developers.sprintsync.domain.track.use_case

import com.developers.sprintsync.data.track.data_source.TrackDataSource
import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksFlowUseCase
    @Inject
    constructor(
        repository: TrackDataSource,
    ) {
        val tracks: Flow<List<Track>> = repository.tracks
    }
