package com.developers.sprintsync.tracking.component.use_case

import com.developers.sprintsync.tracking.service.manager.TrackingStateManager
import javax.inject.Inject

class GetCurrentTrackingStateUseCase
    @Inject
    constructor(
        private val provider: TrackingStateManager,
    ) {
        operator fun invoke() = provider.trackingStateFlow
    }
