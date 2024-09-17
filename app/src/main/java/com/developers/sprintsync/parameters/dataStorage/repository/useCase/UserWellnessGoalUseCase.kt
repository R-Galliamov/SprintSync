package com.developers.sprintsync.parameters.dataStorage.repository.useCase

import com.developers.sprintsync.parameters.dataStorage.repository.UserPreferencesRepositoryImpl
import com.developers.sprintsync.statistics.model.goal.WellnessGoal
import javax.inject.Inject

class UserWellnessGoalUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepositoryImpl,
    ) {
        operator fun invoke() = repository.wellnessGoalFlow

        suspend fun saveWellnessGoal(goal: WellnessGoal) = repository.saveWellnessGoal(goal)
    }
