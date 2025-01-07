package com.developers.sprintsync.update_goals.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.user_parameters.components.use_case.WellnessGoalUseCase
import com.developers.sprintsync.core.components.goal.domain.use_case.GetLastDailyGoalsUseCase
import com.developers.sprintsync.core.components.goal.domain.use_case.SaveDailyGoalUseCase
import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.update_goals.presentation.util.DailyGoalCreator
import com.developers.sprintsync.update_goals.presentation.util.formatter.DailyGoalFormatter
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
        private val wellnessGoalUseCase: WellnessGoalUseCase,
    ) : ViewModel() {
        private var metricValuesMap = mutableMapOf<Metric, Float>()

        val wellnessGoal: Flow<WellnessGoal> = wellnessGoalUseCase()

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
                wellnessGoalUseCase.saveWellnessGoal(goal)
            }
        }

        companion object {
            private const val TAG = "My stack: ViewModel"
        }
    }
