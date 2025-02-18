package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.data_source.TrackDataSource
import javax.inject.Inject

class DeleteTrackByIdUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        suspend operator fun invoke(trackId: Int) = repository.deleteTrackById(trackId)
    }
