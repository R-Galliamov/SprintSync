package com.developers.sprintsync.domain.tracking_service.use_case

import com.developers.sprintsync.domain.tracking_service.internal.managing.TrackingController
import com.developers.sprintsync.domain.tracking_service.model.TrackingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTrackingDataUseCase
    @Inject
    constructor(
        private val provider: TrackingController,
    ) {
        operator fun invoke(): Flow<TrackingData> = provider.trackingDataFlow
    }
