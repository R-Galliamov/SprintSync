package com.developers.sprintsync.domain.user_profile

import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import kotlinx.coroutines.flow.Flow

interface UserParametersRepository {

    fun stream(): Flow<Resource<UserParameters>>

    suspend fun save(parameters: UserParameters): Resource.Result<Unit>
}