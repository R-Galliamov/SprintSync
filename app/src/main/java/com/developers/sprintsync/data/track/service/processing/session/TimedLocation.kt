package com.developers.sprintsync.data.track.service.processing.session

import com.developers.sprintsync.domain.track.model.LocationModel

data class TimedLocation(
    val location: LocationModel,
    val timestampMillis: Long,
)
