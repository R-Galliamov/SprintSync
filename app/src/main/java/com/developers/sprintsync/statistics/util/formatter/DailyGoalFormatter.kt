package com.developers.sprintsync.statistics.util.formatter

import com.developers.sprintsync.statistics.domain.chart.data.Metric
import com.developers.sprintsync.statistics.domain.goal.DailyGoal
import com.developers.sprintsync.statistics.domain.ui.FormattedDailyGoal
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
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
