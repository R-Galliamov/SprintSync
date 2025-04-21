package com.developers.sprintsync.presentation.home_screen

import com.developers.sprintsync.core.util.time.TimeParts
import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.presentation.components.TracksStatsCalculator

data class WorkoutStatistics(
    val runs: String,
    val totalDistance: String,
    val totalKiloCalories: String,
    val longestDistance: String,
    val bestPace: String,
    val maxDuration: TimeParts,
) {
    companion object {
        fun create(tracks: List<Track>) = WorkoutsStatisticsGenerator.generate(tracks)
    }

    private object WorkoutsStatisticsGenerator {
        fun generate(tracks: List<Track>): WorkoutStatistics {
            if (tracks.isEmpty()) return StatsParameters.EMPTY.format()

            val calculator = TracksStatsCalculator(tracks)

            val parameters =
                StatsParameters(
                    totalRuns = calculator.totalWorkouts,
                    totalDistance = calculator.totalDistanceMeters,
                    totalCalories = calculator.totalCaloriesBurned,
                    longestDistance = calculator.longestDistanceMeters,
                    bestPace = calculator.bestPace,
                    maxDuration = calculator.longestDurationMillis,
                )
            return Formatter.format(parameters)
        }

        data class StatsParameters(
            val totalRuns: Int,
            val totalDistance: Float,
            val totalCalories: Float,
            val longestDistance: Float,
            val bestPace: Float,
            val maxDuration: Long,
        ) {
            fun format(): WorkoutStatistics = Formatter.format(this)

            companion object {
                val EMPTY =
                    StatsParameters(
                        totalRuns = 0,
                        totalDistance = 0f,
                        totalCalories = 0f,
                        longestDistance = 0f,
                        bestPace = 0f,
                        maxDuration = 0L,
                    )
            }
        }

        private object Formatter {
            fun format(stat: StatsParameters): WorkoutStatistics =
                WorkoutStatistics(
                    runs = stat.totalRuns.toString(),
                    totalDistance = DistanceUiFormatter.format(stat.totalDistance, DistanceUiPattern.PLAIN),
                    totalKiloCalories = CaloriesUiFormatter.format(stat.totalCalories, CaloriesUiFormatter.Pattern.PLAIN),
                    longestDistance = DistanceUiFormatter.format(stat.longestDistance, DistanceUiPattern.PLAIN),
                    bestPace = PaceUiFormatter.format(stat.bestPace, PaceUiFormatter.Pattern.TWO_DECIMALS),
                    maxDuration = TimeParts.create(stat.maxDuration),
                )
        }
    }
}
