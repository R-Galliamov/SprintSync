package com.developers.sprintsync.user.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.user.dataStorage.repository.dailyGoal.useCase.GetLastDailyGoalsUseCase
import com.developers.sprintsync.user.dataStorage.repository.dailyGoal.useCase.SaveDailyGoalUseCase
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.util.creater.DailyGoalCreator
import com.developers.sprintsync.user.util.formatter.DailyGoalFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateGoalsViewModel
    @Inject
    constructor(
        getLastDailyGoalsUseCase: GetLastDailyGoalsUseCase,
        private val saveDailyGoalUseCase: SaveDailyGoalUseCase,
    ) : ViewModel() {
        private var metricValuesMap: MutableMap<Metric, Float> = mutableMapOf()

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

        companion object {
            private const val TAG = "My stack: ViewModel"
        }
    }
