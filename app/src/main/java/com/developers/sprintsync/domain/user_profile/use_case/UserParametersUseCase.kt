package com.developers.sprintsync.domain.user_profile.use_case

import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserParametersUseCase
    @Inject
    constructor(
        private val repository: UserParametersRepository,
    ) {
        operator fun invoke(): Flow<UserParameters> = repository.getUserParametersFlow().map { it ?: UserParameters.DEFAULT } // TODO replace with state flow
    }
