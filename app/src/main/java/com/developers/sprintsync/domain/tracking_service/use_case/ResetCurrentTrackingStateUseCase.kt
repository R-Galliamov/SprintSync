package com.developers.sprintsync.domain.tracking_service.use_case

import com.developers.sprintsync.domain.tracking_service.internal.managing.TrackingDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResetCurrentTrackingStateUseCase
    @Inject
    constructor(
        private val provider: TrackingDataManager,
    ) {
        operator fun invoke() = provider.resetState()
    }
