package com.developers.sprintsync.tracking.component.use_case

import com.developers.sprintsync.tracking.data.provider.location.LocationProvider
import javax.inject.Inject

class GetLocationFlowUseCase
    @Inject
    constructor(
        private val provider: LocationProvider,
    ) {
        operator fun invoke() = provider.locationFlow()
    }