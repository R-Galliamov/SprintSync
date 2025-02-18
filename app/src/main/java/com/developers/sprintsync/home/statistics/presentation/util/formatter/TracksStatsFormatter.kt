package com.developers.sprintsync.home.statistics.presentation.util.formatter

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.home.statistics.domain.model.TracksStatsCompact
import com.developers.sprintsync.home.statistics.presentation.model.TracksStatsCompactFormatted

class TracksStatsFormatter {
    companion object {
        fun format(stat: TracksStatsCompact): TracksStatsCompactFormatted =
            TracksStatsCompactFormatted(
                runs = stat.runs.toString(),
                totalDistance = DistanceUiFormatter.format(stat.totalDistance, DistanceUiPattern.PLAIN),
                totalKiloCalories = CaloriesFormatter.formatCalories(stat.totalCalories),
                longestDistance = DistanceUiFormatter.format(stat.longestDistance, DistanceUiPattern.PLAIN),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(stat.bestPace),
                maxDuration = DurationFormatter.formatToHhMmAndSs(stat.maxDuration),
            )
    }
}
