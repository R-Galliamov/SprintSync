package com.developers.sprintsync.user_parameters.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.developers.sprintsync.core.database.dataStore
import com.developers.sprintsync.user_parameters.data.util.converter.GenderStorageConverter
import com.developers.sprintsync.user_parameters.data.util.converter.WellnessGoalStorageConverter
import com.developers.sprintsync.user_parameters.components.model.Gender
import com.developers.sprintsync.user_parameters.model.UserParameters
import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val data = context.dataStore.data

        val genderFlow: Flow<Gender> = data.map { getGender(it) }

        val birthDateFlow: Flow<Long> = data.map { getBirthDate(it) }

        val weightFlow: Flow<Float> = data.map { getWeight(it) }

        val wellnessGoalFlow: Flow<WellnessGoal> = data.map { getWellnessGoal(it) }

        val useStatsPermissionFlow: Flow<Boolean> = data.map { getUseStatsPermission(it) }

        val parametersFlow =
            combine(
                genderFlow,
                birthDateFlow,
                weightFlow,
                wellnessGoalFlow,
                useStatsPermissionFlow,
            ) { gender, birthDate, weight, wellnessGoal, useStatsPermission ->
                UserParameters(gender, birthDate, weight, wellnessGoal, useStatsPermission)
            }

        suspend fun saveParameters(parameters: UserParameters) {
            saveGender(parameters.gender)
            saveBirthDate(parameters.birthDateTimestamp)
            saveWeight(parameters.weight)
            saveWellnessGoal(parameters.wellnessGoal)
            saveUseStatsPermission(parameters.useStatsPermission)
        }

        suspend fun saveGender(gender: Gender) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.GENDER] = GenderStorageConverter.fromGender(gender)
            }
        }

        suspend fun saveBirthDate(birthDate: Long) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.BIRTH_DATE] = birthDate
            }
        }

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

        suspend fun saveUseStatsPermission(permission: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.USE_STATS_PERMISSION] = permission
            }
        }

        private fun getGender(preferences: Preferences): Gender {
            val gender = preferences[PreferencesKeys.GENDER] ?: DEFAULT_GENDER
            return GenderStorageConverter.toGender(gender)
        }

        private fun getBirthDate(preferences: Preferences): Long {
            val birthDate = preferences[PreferencesKeys.BIRTH_DATE] ?: DEFAULT_BIRTH_DATE
            return birthDate
        }

        private fun getWeight(preferences: Preferences) = preferences[PreferencesKeys.WEIGHT] ?: DEFAULT_WEIGHT

        private fun getWellnessGoal(preferences: Preferences): WellnessGoal {
            val goal = preferences[PreferencesKeys.WELLNESS_GOAL] ?: DEFAULT_WELLNESS_GOAL
            return WellnessGoalStorageConverter.toWellnessGoal(goal)
        }

        private fun getUseStatsPermission(preferences: Preferences) =
            preferences[PreferencesKeys.USE_STATS_PERMISSION] ?: DEFAULT_USE_STATS_PERMISSION

        companion object {
            private val DEFAULT_GENDER = GenderStorageConverter.fromGender(Gender.MALE)
            private val DEFAULT_BIRTH_DATE =
                Calendar
                    .getInstance()
                    .apply {
                        set(Calendar.YEAR, 2000)
                        set(Calendar.MONTH, 0)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }.timeInMillis
            private const val DEFAULT_WEIGHT = 70.0f
            private val DEFAULT_WELLNESS_GOAL =
                WellnessGoalStorageConverter.fromWellnessGoal(WellnessGoal.MAINTAIN_WEIGHT)
            private const val DEFAULT_USE_STATS_PERMISSION = true
        }
    }
