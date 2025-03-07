package com.developers.sprintsync.update_goals.presentation.util.formatter

import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.statistics.presentation.model.FormattedDailyGoal

class DailyGoalFormatter {
    companion object {
        fun format(dailyGoal: DailyGoal): FormattedDailyGoal {
            val value =
                when (dailyGoal.metricType) {
                    Metric.DISTANCE -> {
                        val distance = dailyGoal.value
                        DistanceUiFormatter.format(distance, DistanceUiPattern.PLAIN)
                    }

                    Metric.DURATION -> DurationUiFormatter.format(dailyGoal.value.toLong(), DurationUiPattern.MM)
                    Metric.CALORIES -> dailyGoal.value.toInt().toString()
                }
            return FormattedDailyGoal(dailyGoal.metricType, value)
        }
    }
}
