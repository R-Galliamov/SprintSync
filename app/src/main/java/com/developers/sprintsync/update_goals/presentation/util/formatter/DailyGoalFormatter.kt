package com.developers.sprintsync.update_goals.presentation.util.formatter

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.core.components.goal.data.model.DailyGoal
import com.developers.sprintsync.statistics.presentation.model.FormattedDailyGoal
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import kotlin.math.roundToInt

class DailyGoalFormatter {
    companion object {
        fun format(dailyGoal: DailyGoal): FormattedDailyGoal {
            val value =
                when (dailyGoal.metricType) {
                    Metric.DISTANCE ->
                        DistanceFormatter.metersToPresentableKilometers(
                            dailyGoal.value.roundToInt(),
                            false,
                        )

                    Metric.DURATION -> DurationFormatter.formatToMm(dailyGoal.value.toLong(), false)
                    Metric.CALORIES -> dailyGoal.value.toInt().toString()
                }
            return FormattedDailyGoal(dailyGoal.metricType, value)
        }
    }
}
