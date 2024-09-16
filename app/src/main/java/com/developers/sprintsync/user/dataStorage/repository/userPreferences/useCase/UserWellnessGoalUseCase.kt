package com.developers.sprintsync.user.dataStorage.repository.userPreferences.useCase

import com.developers.sprintsync.user.dataStorage.repository.userPreferences.UserPreferencesRepositoryImpl
import com.developers.sprintsync.user.model.goal.WellnessGoal
import javax.inject.Inject

class UserWellnessGoalUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepositoryImpl,
    ) {
        operator fun invoke() = repository.wellnessGoalFlow

        suspend fun saveWellnessGoal(goal: WellnessGoal) = repository.saveWellnessGoal(goal)
    }
