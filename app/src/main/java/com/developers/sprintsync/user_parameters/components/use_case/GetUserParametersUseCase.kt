package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserParametersRepository
import com.developers.sprintsync.user_parameters.model.UserParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetUserParametersUseCase
    @Inject
    constructor(
        repository: UserParametersRepository,
    ) {
        val parametersFlow: Flow<UserParameters> =
            combine(
                repository.genderFlow,
                repository.birthDateFlow,
                repository.weightKgFlow,
                repository.wellnessGoalFlow,
                repository.useStatsPermissionFlow,
            ) { gender, birthDate, weight, wellnessGoal, useStatsPermission ->
                UserParameters(gender, birthDate, weight, wellnessGoal, useStatsPermission)
            }
    }
