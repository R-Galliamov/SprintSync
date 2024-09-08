package com.developers.sprintsync.tracking.analytics.dataManager.calculator

import java.util.concurrent.TimeUnit

object DurationCalculator {
    fun minutesToMillis(minutes: Long): Long = TimeUnit.MINUTES.toMillis(minutes)
}
