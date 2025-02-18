package com.developers.sprintsync.tracking.data.flow

import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.provider.location.LocationProvider
import javax.inject.Inject

class LocationFlowManager
    @Inject
    constructor(
        provider: LocationProvider,
    ) : FlowManager<LocationModel>(provider.locationFlow())