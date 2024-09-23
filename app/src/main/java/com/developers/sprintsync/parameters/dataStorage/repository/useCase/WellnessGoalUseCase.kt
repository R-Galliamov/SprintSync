package com.developers.sprintsync.parameters.dataStorage.repository.useCase

import com.developers.sprintsync.parameters.dataStorage.repository.UserPreferencesRepository
import com.developers.sprintsync.statistics.model.goal.WellnessGoal
import javax.inject.Inject

class WellnessGoalUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        operator fun invoke() = repository.wellnessGoalFlow

        suspend fun saveWellnessGoal(goal: WellnessGoal) = repository.saveWellnessGoal(goal)
    }
