package com.developers.sprintsync.tracking.analytics.dataManager.formatter

import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.analytics.model.FormattedStatistics
import com.developers.sprintsync.tracking.analytics.model.TrackingStatistics

class TrackingStatisticsFormatter {
    companion object {
        fun format(stat: TrackingStatistics): FormattedStatistics =
            FormattedStatistics(
                runs = stat.runs.toString(),
                totalDistance = DistanceMapper.metersToPresentableKilometers(stat.totalDistance),
                totalCalories = stat.totalCalories.toString(),
                longestDistance = DistanceMapper.metersToPresentableKilometers(stat.longestDistance),
                bestPace = PaceMapper.formatPaceWithTwoDecimals(stat.bestPace),
                maxDuration = DurationFormatter.formatToHhMmAndSs(stat.maxDuration),
            )
    }
}
