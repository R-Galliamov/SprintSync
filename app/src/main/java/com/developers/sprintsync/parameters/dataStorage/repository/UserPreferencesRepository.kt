package com.developers.sprintsync.parameters.dataStorage.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.developers.sprintsync.global.dataStorage.preferences.dataStore
import com.developers.sprintsync.statistics.model.goal.WellnessGoal
import com.developers.sprintsync.statistics.util.converter.WellnessGoalStorageConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val data = context.dataStore.data

        val weightFlow: Flow<Float> = data.map { getWeight(it) }

        val wellnessGoalFlow: Flow<WellnessGoal> = data.map { getWellnessGoal(it) }

        suspend fun saveWeight(weight: Float) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.WEIGHT] = weight
            }
        }

        suspend fun saveWellnessGoal(goal: WellnessGoal) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.WELLNESS_GOAL] = WellnessGoalStorageConverter.fromWellnessGoal(goal)
            }
        }

        private fun getWeight(preferences: Preferences) = preferences[PreferencesKeys.WEIGHT] ?: DEFAULT_WEIGHT

        private fun getWellnessGoal(preferences: Preferences): WellnessGoal {
            val goal = preferences[PreferencesKeys.WELLNESS_GOAL] ?: DEFAULT_WELLNESS_GOAL
            return WellnessGoalStorageConverter.toWellnessGoal(goal)
        }

        companion object {
            private const val DEFAULT_WEIGHT = 70.0f
            private val DEFAULT_WELLNESS_GOAL =
                WellnessGoalStorageConverter.fromWellnessGoal(WellnessGoal.MAINTAIN_WEIGHT)
        }
    }
