package com.developers.sprintsync.tracking.analytics.dataManager.formatter

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import com.developers.sprintsync.tracking.analytics.model.FormattedStatistics
import com.developers.sprintsync.tracking.analytics.model.TrackingStatistics

class TrackingStatisticsFormatter {
    companion object {
        fun format(stat: TrackingStatistics): FormattedStatistics =
            FormattedStatistics(
                runs = stat.runs.toString(),
                totalDistance = DistanceFormatter.metersToPresentableKilometers(stat.totalDistance),
                totalKiloCalories = CaloriesFormatter.formatCalories(stat.totalCalories),
                longestDistance = DistanceFormatter.metersToPresentableKilometers(stat.longestDistance),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(stat.bestPace),
                maxDuration = DurationFormatter.formatToHhMmAndSs(stat.maxDuration),
            )
    }
}
