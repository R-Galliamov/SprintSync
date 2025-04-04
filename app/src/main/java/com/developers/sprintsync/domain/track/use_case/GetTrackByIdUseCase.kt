package com.developers.sprintsync.domain.track.use_case

import com.developers.sprintsync.data.track.data_source.TrackDataSource
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        suspend operator fun invoke(trackId: Int) = repository.getTrackById(trackId)
    }
