package com.developers.sprintsync.core.components.track.domain.metrics_converter

import java.util.concurrent.TimeUnit

object DurationConverter {
    fun minutesToMillis(minutes: Long): Long = TimeUnit.MINUTES.toMillis(minutes)
}
