package com.developers.sprintsync.data.user_parameters.repository

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.user_parameters.source.UserParametersDataSource
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user parameters data.
 */
interface UserParametersRepository {

    /**
     * Provides a flow of user parameters, or null if not set.
     * @return Flow emitting the current [UserParameters] or null.
     */
    fun getUserParametersFlow(): Flow<UserParameters?>

    /**
     * Updates the user parameters.
     * @param parameters The [UserParameters] to save.
     */
    suspend fun updateUserParameters(parameters: UserParameters)
}

/**
 * Implementation of [UserParametersRepository] using a local data source.
 */
class UserParametersRepositoryImpl(
    private val localDataSource: UserParametersDataSource,
    private val log: AppLogger,
) : UserParametersRepository {
    override fun getUserParametersFlow(): Flow<UserParameters?> = localDataSource.loadUserParameters()

    override suspend fun updateUserParameters(parameters: UserParameters) {
        try {
            localDataSource.saveUserParameters(parameters)
            log.i("User parameters updated: $parameters")
        } catch (e: Exception) {
            log.e("Failed to save user parameters: ${e.message}", e)
        }
    }
}
