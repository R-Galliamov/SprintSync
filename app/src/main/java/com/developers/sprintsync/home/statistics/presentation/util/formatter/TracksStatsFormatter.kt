package com.developers.sprintsync.home.statistics.presentation.util.formatter

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.util.time.TimeParts
import com.developers.sprintsync.home.statistics.domain.model.TracksStatsCompact
import com.developers.sprintsync.home.statistics.presentation.model.TracksStatsCompactFormatted
import javax.inject.Inject

class TracksStatsFormatter
    @Inject
    constructor() {
        fun format(stat: TracksStatsCompact): TracksStatsCompactFormatted =
            TracksStatsCompactFormatted(
                runs = stat.runs.toString(),
                totalDistance = DistanceUiFormatter.format(stat.totalDistance, DistanceUiPattern.PLAIN),
                totalKiloCalories = CaloriesFormatter.formatCalories(stat.totalCalories),
                longestDistance = DistanceUiFormatter.format(stat.longestDistance, DistanceUiPattern.PLAIN),
                bestPace = PaceFormatter.formatPaceWithTwoDecimals(stat.bestPace),
                maxDuration = TimeParts.create(stat.maxDuration),
            )
    }
