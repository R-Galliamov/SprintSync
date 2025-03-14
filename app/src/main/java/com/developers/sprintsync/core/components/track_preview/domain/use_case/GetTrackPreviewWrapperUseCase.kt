package com.developers.sprintsync.core.components.track_preview.domain.use_case

import com.developers.sprintsync.core.components.track_preview.data.repository.TrackPreviewRepository
import javax.inject.Inject

class GetTrackPreviewWrapperUseCase
    @Inject
    constructor(
        private val repository: TrackPreviewRepository,
    ) {
        operator fun invoke() = repository.trackPreviewWrappersFlow
    }
