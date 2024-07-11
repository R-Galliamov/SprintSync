package com.developers.sprintsync.tracking.session.service.dataBuilder.calculator

import com.developers.sprintsync.tracking.data.mapper.indicator.DistanceMapper
import com.developers.sprintsync.user.model.UserSettings
import javax.inject.Inject

class CaloriesCalculator
    @Inject
    constructor(
        private val userSettings: UserSettings,
    ) {
        // TODO find better formula that includes speed
        fun getBurnedKiloCalories(distanceMeters: Int): Int =
            (DistanceMapper.metersToKilometers(distanceMeters) * userSettings.weight).toInt()
    }
