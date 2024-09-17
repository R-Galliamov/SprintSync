package com.developers.sprintsync.tracking.session.service.dataBuilder.calculator

import com.developers.sprintsync.parameters.model.UserSettings
import javax.inject.Inject
import kotlin.math.roundToInt

class CaloriesCalculator
    @Inject
    constructor(
        private val userSettings: UserSettings,
    ) {
        // TODO find better formula that includes speed
        fun getBurnedCalories(distanceMeters: Int): Int = (distanceMeters * userSettings.weight).roundToInt()
    }
