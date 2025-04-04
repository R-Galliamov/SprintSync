package com.developers.sprintsync.domain.track_preview.use_case

import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import javax.inject.Inject

class GetTrackPreviewWrapperUseCase
    @Inject
    constructor(
        private val repository: TrackPreviewRepository,
    ) {
        operator fun invoke() = repository.trackPreviewWrappersFlow
    }
