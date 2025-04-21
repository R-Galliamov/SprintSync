package com.developers.sprintsync.data.user_parameters.repository

import android.util.Log
import com.developers.sprintsync.data.user_parameters.source.UserParametersDataSource
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import kotlinx.coroutines.flow.Flow

interface UserParametersRepository {
    fun getUserParametersFlow(): Flow<UserParameters?>

    suspend fun updateUserParameters(parameters: UserParameters)
}

class UserParametersRepositoryImpl(
    private val localDataSource: UserParametersDataSource,
) : UserParametersRepository {
    override fun getUserParametersFlow(): Flow<UserParameters?> = localDataSource.loadUserParameters()

    override suspend fun updateUserParameters(parameters: UserParameters) {
        try {
            localDataSource.saveUserParameters(parameters)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving parameters", e)
        }
    }

    companion object {
        private const val TAG = "UserParametersRepository"
    }
}
