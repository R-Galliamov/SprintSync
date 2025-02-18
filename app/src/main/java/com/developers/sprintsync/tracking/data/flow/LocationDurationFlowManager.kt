package com.developers.sprintsync.tracking.data.flow

import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.tracking.data.provider.location.LocationProvider
import com.developers.sprintsync.tracking.data.provider.time.TrackingTimeProvider
import com.developers.sprintsync.core.util.extension.withLatestConcat
import javax.inject.Inject

class LocationDurationFlowManager
    @Inject
    constructor(
        locationProvider: LocationProvider,
        timeProvider: TrackingTimeProvider,
    ) : FlowManager<TimedLocation>(
            locationProvider
                .locationFlow()
                .withLatestConcat(timeProvider.timeInMillisFlow()) { location, timeMillis ->
                    TimedLocation(location, timeMillis)
                },
        )