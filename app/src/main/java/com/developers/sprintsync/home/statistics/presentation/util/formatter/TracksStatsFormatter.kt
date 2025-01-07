package com.developers.sprintsync.home.statistics.presentation.util.formatter

import com.developers.sprintsync.home.statistics.presentation.model.TracksStatsCompactFormatted
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.home.statistics.domain.model.TracksStatsCompact

class TracksStatsFormatter {
    companion object {
        fun format(stat: TracksStatsCompact): TracksStatsCompactFormatted =
            TracksStatsCompactFormatted(
                runs = stat.runs.toString(),
                totalDistance = DistanceFormatter.metersToPresentableKilometers(stat.totalDistance),
                totalKiloCalories = CaloriesFormatter.formatCalories(stat.totalCalories),
                longestDistance = DistanceFormatter.metersToPresentableKilometers(stat.longestDistance),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(stat.bestPace),
                maxDuration = DurationFormatter.formatToHhMmAndSs(stat.maxDuration),
            )
    }
}
