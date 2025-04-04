package com.developers.sprintsync.domain.user_parameters.use_case

import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepository
import com.developers.sprintsync.domain.user_parameters.model.WellnessGoal
import javax.inject.Inject

class WellnessGoalUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        operator fun invoke() = repository.wellnessGoalFlow

        suspend fun saveWellnessGoal(goal: WellnessGoal) = repository.saveWellnessGoal(goal)
    }
