package com.developers.sprintsync.parameters.dataStorage.repository.useCase

import com.developers.sprintsync.parameters.dataStorage.repository.UserPreferencesRepository
import com.developers.sprintsync.parameters.model.UserParameters
import javax.inject.Inject

class PersonalParametersUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        val parametersFlow = repository.parametersFlow

        suspend fun saveParameters(parameters: UserParameters) = repository.saveParameters(parameters)
    }
