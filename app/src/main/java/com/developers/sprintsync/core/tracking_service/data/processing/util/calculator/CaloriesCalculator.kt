package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator

import com.developers.sprintsync.user_parameters.components.model.UserSettings
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
