package com.developers.sprintsync.domain.user_profile.use_case

import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import javax.inject.Inject

class UpdateUserParametersUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        suspend operator fun invoke(parameters: UserParameters) = repository.updateUserParameters(parameters)
    }
