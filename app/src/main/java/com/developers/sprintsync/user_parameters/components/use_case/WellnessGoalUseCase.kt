package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserPreferencesRepository
import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import javax.inject.Inject

class WellnessGoalUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        operator fun invoke() = repository.wellnessGoalFlow

        suspend fun saveWellnessGoal(goal: WellnessGoal) = repository.saveWellnessGoal(goal)
    }
