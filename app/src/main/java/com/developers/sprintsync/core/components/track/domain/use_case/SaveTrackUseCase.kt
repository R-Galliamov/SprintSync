package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.data_source.TrackDataSource
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class SaveTrackUseCase
    @Inject
    constructor(
        private val repository: TrackDataSource,
    ) {
        suspend operator fun invoke(track: Track) : Int = repository.saveTrack(track)
    }
