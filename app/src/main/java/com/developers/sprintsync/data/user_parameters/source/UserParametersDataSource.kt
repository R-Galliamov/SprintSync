package com.developers.sprintsync.data.user_parameters.source

import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import kotlinx.coroutines.flow.Flow

interface UserParametersDataSource {
    suspend fun saveUserParameters(params: UserParameters)

    fun loadUserParameters(): Flow<UserParameters?>
}
