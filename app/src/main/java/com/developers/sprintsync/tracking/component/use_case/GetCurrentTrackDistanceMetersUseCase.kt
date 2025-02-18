package com.developers.sprintsync.tracking.component.use_case

import com.developers.sprintsync.tracking.service.manager.TrackingStateManager
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentTrackDistanceMetersUseCase
    @Inject
    constructor(
        private val provider: TrackingStateManager,
    ) {
        operator fun invoke() = provider.trackingStateFlow.map { it.track.distanceMeters }
    }