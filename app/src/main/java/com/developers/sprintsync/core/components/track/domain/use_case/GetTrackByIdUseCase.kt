package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.data_source.TrackDataSource
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        operator fun invoke(trackId: Int) = repository.getTrackById(trackId)
    }
