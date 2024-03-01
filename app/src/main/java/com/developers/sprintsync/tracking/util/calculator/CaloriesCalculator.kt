package com.developers.sprintsync.tracking.util.calculator

import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.user.model.UserSettings
import javax.inject.Inject

class CaloriesCalculator
    @Inject
    constructor(private val userSettings: UserSettings) {
        // TODO find better formula that includes speed
        fun getBurnedKiloCalories(distanceMeters: Int): Int {
            return (DistanceMapper.metersToKilometers(distanceMeters) * userSettings.weight).toInt()
        }
    }
