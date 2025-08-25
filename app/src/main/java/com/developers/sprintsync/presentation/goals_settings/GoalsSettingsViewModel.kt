package com.developers.sprintsync.presentation.goals_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.domain.workouts_plan.use_case.GetLastDailyGoalsUseCase
import com.developers.sprintsync.domain.workouts_plan.use_case.SaveDailyGoalUseCase
import com.developers.sprintsync.domain.user_profile.model.WellnessGoal
import com.developers.sprintsync.domain.user_profile.use_case.UpdateUserParametersUseCase
import com.developers.sprintsync.domain.user_profile.use_case.UserParametersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsSettingsViewModel
    @Inject
    constructor(
        getLastDailyGoalsUseCase: GetLastDailyGoalsUseCase,
        private val saveDailyGoalUseCase: SaveDailyGoalUseCase,
    ) : ViewModel() {
        private var metricValuesMap = mutableMapOf<Metric, Float>()

        val dailyGoals =
            getLastDailyGoalsUseCase.invoke().map { goals ->
                goals.mapValues { entry ->
                    val metric = entry.value.value
                    val dailyGoal = entry.value
                    metricValuesMap[entry.key] = metric
                    DailyGoalDisplayMode.create(dailyGoal)
                }
            }

        fun updateValues(values: Map<Metric, Float>) {
            viewModelScope.launch {
                values
                    .filterNot { (metric, value) ->
                        metricValuesMap.any { (k, v) -> k == metric && v == value }
                    }.forEach {
                        // TODO pass daily goal. Refactor UI logic
                        val displayMode = DailyGoalDisplayMode(metric = it.key, it.value.toString())
                        val dailyGoal = displayMode.toDailyGoal()
                        saveDailyGoalUseCase(dailyGoal)
                    }
            }
        }

        companion object {
            private const val TAG = "My stack: ViewModel"
        }
    }
