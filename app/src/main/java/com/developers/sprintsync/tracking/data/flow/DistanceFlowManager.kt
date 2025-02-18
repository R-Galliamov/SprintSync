package com.developers.sprintsync.tracking.data.flow

import com.developers.sprintsync.tracking.component.use_case.GetCurrentTrackDistanceMetersUseCase
import javax.inject.Inject

class DistanceFlowManager
    @Inject
    constructor(
        provider: GetCurrentTrackDistanceMetersUseCase,
    ) : FlowManager<Int>(provider())