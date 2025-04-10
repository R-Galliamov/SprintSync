package com.developers.sprintsync.domain.tracking_service.use_case

import com.developers.sprintsync.domain.tracking_service.internal.managing.TrackingController
import javax.inject.Inject

class GetSessionDataUseCase
    @Inject
    constructor(
        private val provider: TrackingController,
    ) {
        operator fun invoke() = provider.sessionDataFlow
    }
