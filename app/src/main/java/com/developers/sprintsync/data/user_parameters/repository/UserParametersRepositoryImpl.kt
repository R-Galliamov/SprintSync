package com.developers.sprintsync.data.user_parameters.repository

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.core.mapResourceNotNull
import com.developers.sprintsync.domain.core.runResult
import com.developers.sprintsync.domain.core.toResource
import com.developers.sprintsync.data.user_parameters.model.toDomain
import com.developers.sprintsync.data.user_parameters.model.toStored
import com.developers.sprintsync.data.user_parameters.source.UserParametersDataSource
import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.user_profile.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [UserParametersRepository] using a local data source.
 */
class UserParametersRepositoryImpl(
    private val ds: UserParametersDataSource,
    private val log: AppLogger,
) : UserParametersRepository {
    override fun stream(): Flow<Resource<UserParameters>> = ds.load().toResource().mapResourceNotNull { it.toDomain() }

    override suspend fun save(parameters: UserParameters): Resource.Result<Unit> =
        runResult { ds.save(parameters.toStored()) }
}
