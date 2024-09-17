package com.developers.sprintsync.statistics.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.parameters.dataStorage.repository.useCase.UserWellnessGoalUseCase
import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase.GetLastDailyGoalsUseCase
import com.developers.sprintsync.statistics.dataStorage.repository.dailyGoal.useCase.SaveDailyGoalUseCase
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.goal.WellnessGoal
import com.developers.sprintsync.statistics.util.creater.DailyGoalCreator
import com.developers.sprintsync.statistics.util.formatter.DailyGoalFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateGoalsViewModel
    @Inject
    constructor(
        getLastDailyGoalsUseCase: GetLastDailyGoalsUseCase,
        private val saveDailyGoalUseCase: SaveDailyGoalUseCase,
        private val userWellnessGoalUseCase: UserWellnessGoalUseCase,
    ) : ViewModel() {
        private var metricValuesMap = mutableMapOf<Metric, Float>()

        val wellnessGoal: Flow<WellnessGoal> = userWellnessGoalUseCase()

        val formattedDailyGoals =
            getLastDailyGoalsUseCase.invoke().map { goals ->
                goals.mapValues { entry ->
                    Log.d(TAG, entry.toString())
                    metricValuesMap[entry.key] = entry.value.value
                    DailyGoalFormatter.format(entry.value)
                }
            }

        fun updateValues(values: Map<Metric, Float>) {
            viewModelScope.launch {
                values
                    .filterNot { (metric, value) ->
                        metricValuesMap.any { (k, v) -> k == metric && v == value }
                    }.forEach {
                        val dailyGoal = DailyGoalCreator.create(it.key, it.value)
                        saveDailyGoalUseCase(dailyGoal)
                    }
            }
        }

        fun saveWellnessGoal(goal: WellnessGoal) {
            viewModelScope.launch {
                userWellnessGoalUseCase.saveWellnessGoal(goal)
            }
        }

        companion object {
            private const val TAG = "My stack: ViewModel"
        }
    }
