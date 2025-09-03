package com.developers.sprintsync.presentation.home_screen

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.presentation.components.TimeParts
import com.developers.sprintsync.presentation.components.formatter.CaloriesUiFormatter
import com.developers.sprintsync.presentation.components.formatter.PaceUiFormatter
import com.developers.sprintsync.domain.statistics.model.WorkoutsStats
import com.developers.sprintsync.presentation.components.formatter.DistanceFormatter
import javax.inject.Inject

data class RecordBoardUiModel(
    val totalWorkouts: String,
    val totalDistance: String,
    val totalCalories: String,
    val longestDistance: String,
    val peakPace: String,
    val maxDuration: TimeParts,
) {
    companion object {
        val EMPTY = RecordBoardUiModel(
            totalWorkouts = "",
            totalDistance = "",
            totalCalories = "",
            longestDistance = "",
            peakPace = "",
            maxDuration = TimeParts(0, 0, 0)
        )
    }
}

class RecordBoardCreator @Inject constructor(
    private val distanceFormatter: DistanceFormatter,
    private val log: AppLogger
) {

    fun create(stats: WorkoutsStats): RecordBoardUiModel {
        try {
            val totalWorkouts = stats.totalWorkouts.toString()
            val totalDistance = distanceFormatter.format(stats.totalDistanceMeters).value
            val totalCalories = CaloriesUiFormatter.format(stats.totalCalories, CaloriesUiFormatter.Pattern.PLAIN)
            val longestDistance = distanceFormatter.format(stats.longestDistanceMeters).value
            val peakPace = PaceUiFormatter.format(stats.peakPaceMPKm, PaceUiFormatter.Pattern.TWO_DECIMALS)
            val maxDuration = TimeParts.create(stats.totalDurationMillis)
            return RecordBoardUiModel(
                totalWorkouts = totalWorkouts,
                totalDistance = totalDistance,
                totalCalories = totalCalories,
                longestDistance = longestDistance,
                peakPace = peakPace,
                maxDuration = maxDuration,
            )
        } catch (e: Exception) {
            return RecordBoardUiModel.EMPTY
        }
    }
}


