package com.developers.sprintsync.data.user_parameters.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.user_parameters.model.StoredUserParameters
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data source for managing user parameters using local storage.
 */
interface UserParametersDataSource {
    /**
     * Saves the user parameters to local storage.
     * @param params The [UserParameters] to save.
     */
    suspend fun save(params: StoredUserParameters)

    /**
     * Loads user parameters from local storage as a flow.
     * @return Flow emitting the current [UserParameters] or null if not available or parsing fails.
     */
    fun load(): Flow<StoredUserParameters?>
}

/**
 * Implementation of [UserParametersDataSource] using DataStore and Gson for local persistence.
 */
class LocalUserParametersDataSource(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson,
    private val log: AppLogger
) : UserParametersDataSource {
    override suspend fun save(params: StoredUserParameters) {
        try {
            val str = gson.toJson(params)
            dataStore.edit { it[STORAGE_KEY] = str }
            log.i("User parameters saved: $params")
        } catch (e: Exception) {
            log.e("Failed to save user parameters: ${e.message}", e)
        }
    }

    override fun load(): Flow<StoredUserParameters?> =
        dataStore.data.map { prefs ->
            prefs[STORAGE_KEY]?.let {
                runCatching { gson.fromJson(it, StoredUserParameters::class.java) }
                    .onFailure { e ->
                        log.e("Failed to parse user parameters: ${e.message}", e)
                    }.getOrNull()
            }
        }

    companion object {
        private val STORAGE_KEY = stringPreferencesKey("user_parameters")
    }
}

