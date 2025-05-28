package com.developers.sprintsync.presentation.goals_settings

import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.goal.model.DailyGoal
import com.developers.sprintsync.domain.goal.model.Metric

/**
 * Data model for displaying a daily goal in the UI.
 */
data class DailyGoalDisplayMode(
    val metric: Metric,
    val value: String,
) {
    /**
     * Converts this display model to a [DailyGoal] domain model.
     * @return The converted [DailyGoal].
     * @throws NumberFormatException if the value cannot be converted to a float.
     */
    fun toDailyGoal() = Formatter.toDailyGoal(this)

    companion object {
        /**
         * Creates a [DailyGoalDisplayModel] from a [DailyGoal].
         * @param dailyGoal The [DailyGoal] to convert.
         * @return The formatted [DailyGoalDisplayModel].
         */
        fun create(dailyGoal: DailyGoal) = Formatter.toDisplayMode(dailyGoal)
    }

    private object Formatter {
        fun toDisplayMode(dailyGoal: DailyGoal): DailyGoalDisplayMode {
            val value =
                when (dailyGoal.metricType) {
                    Metric.DISTANCE -> {
                        val distance = dailyGoal.value
                        DistanceUiFormatter.format(distance, DistanceUiPattern.PLAIN)
                    }

                    Metric.DURATION -> DurationUiFormatter.format(dailyGoal.value.toLong(), DurationUiPattern.MM)
                    Metric.CALORIES -> dailyGoal.value.toInt().toString()
                }
            return DailyGoalDisplayMode(dailyGoal.metricType, value)
        }

        // TODO use try when use because of toFloat
        // TODO Move class to independent class to use logger?
        fun toDailyGoal(displayMode: DailyGoalDisplayMode) =
            DailyGoal(
                DEFAULT_DAILY_GOAL_ID,
                System.currentTimeMillis(),
                displayMode.metric,
                displayMode.value.toFloat(),
            )

        private const val DEFAULT_DAILY_GOAL_ID = 0
    }
}
