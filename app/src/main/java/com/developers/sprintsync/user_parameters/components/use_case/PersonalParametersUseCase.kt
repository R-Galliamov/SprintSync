package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserPreferencesRepository
import com.developers.sprintsync.user_parameters.model.UserParameters
import javax.inject.Inject

class PersonalParametersUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        val parametersFlow = repository.parametersFlow

        suspend fun saveParameters(parameters: UserParameters) = repository.saveParameters(parameters)
    }
