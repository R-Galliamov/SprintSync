package com.developers.sprintsync.user_parameters.components.use_case

import com.developers.sprintsync.user_parameters.data.repository.UserParametersRepository
import com.developers.sprintsync.user_parameters.model.UserParameters
import javax.inject.Inject

class SaveUserParametersUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        suspend operator fun invoke(parameters: UserParameters) = repository.saveParameters(parameters)
    }
