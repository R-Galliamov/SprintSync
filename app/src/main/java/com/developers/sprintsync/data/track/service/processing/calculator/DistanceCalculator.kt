package com.developers.sprintsync.data.track.service.processing.calculator

import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.distanceBetweenInMeters
import javax.inject.Inject

interface DistanceCalculator {
    fun distanceBetweenInMeters(start: LocationModel, end: LocationModel): Float
}

class DefaultDistanceCalculator @Inject constructor() : DistanceCalculator {
    override fun distanceBetweenInMeters(start: LocationModel, end: LocationModel): Float =
        start.distanceBetweenInMeters(end)
}