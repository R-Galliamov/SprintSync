package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.data_source.TrackDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLastTrackUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        operator fun invoke() = repository.tracks.map { it.firstOrNull() }
    }
