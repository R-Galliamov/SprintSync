package com.developers.sprintsync.data.user_parameters.source

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserParametersDataSource(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson,
) : UserParametersDataSource {
    override suspend fun saveUserParameters(params: UserParameters) {
        val jsonString = gson.toJson(params)
        dataStore.edit { prefs ->
            prefs[STORAGE_KEY] = jsonString
        }
        Log.d(TAG, "User parameters saved successfully")
    }

    override fun loadUserParameters(): Flow<UserParameters?> =
        dataStore.data.map { prefs ->
            prefs[STORAGE_KEY]?.let { jsonString ->
                runCatching { gson.fromJson(jsonString, UserParameters::class.java) }
                    .onFailure {
                        Log.e("LocalSource", "Failed to parse user parameters", it)
                    }.getOrNull()
            }
        }

    companion object {
        private val STORAGE_KEY = stringPreferencesKey("user_parameters")
        private const val TAG = "LocalUserParametersDataSource"
    }
}
