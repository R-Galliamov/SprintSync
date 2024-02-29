package com.developers.sprintsync.util.calculator

import com.developers.sprintsync.user.model.UserSettings
import com.developers.sprintsync.util.mapper.indicator.DistanceMapper
import javax.inject.Inject

class CaloriesCalculator
    @Inject
    constructor(private val userSettings: UserSettings) {
        // TODO find better formula that includes speed
        fun getBurnedKiloCalories(distanceMeters: Int): Int {
            return (DistanceMapper.metersToKilometers(distanceMeters) * userSettings.weight).toInt()
        }
    }
