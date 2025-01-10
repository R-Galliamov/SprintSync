package com.developers.sprintsync.user_parameters.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.developers.sprintsync.core.database.dataStore
import com.developers.sprintsync.statistics.domain.goal.WellnessGoal
import com.developers.sprintsync.user_parameters.components.cache.data.CachedUserParametersManager
import com.developers.sprintsync.user_parameters.components.model.Gender
import com.developers.sprintsync.user_parameters.data.util.converter.GenderStorageConverter
import com.developers.sprintsync.user_parameters.data.util.converter.WellnessGoalStorageConverter
import com.developers.sprintsync.user_parameters.model.UserParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserParametersRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val cache: CachedUserParametersManager,
    ) {
        private val data = context.dataStore.data

        val genderFlow: Flow<Gender> = data.map { getGender(it) }

        val birthDateFlow: Flow<Long> = data.map { getBirthDate(it) }

        val weightKgFlow: Flow<Float> = data.map { getWeight(it) }

        val wellnessGoalFlow: Flow<WellnessGoal> = data.map { getWellnessGoal(it) }

        val useStatsPermissionFlow: Flow<Boolean> = data.map { getUseStatsPermission(it) }

        suspend fun saveParameters(parameters: UserParameters) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.BIRTH_DATE] = parameters.birthDateTimestamp
                preferences[PreferencesKeys.WEIGHT_KG] = parameters.weight
                preferences[PreferencesKeys.WELLNESS_GOAL] =
                    WellnessGoalStorageConverter.fromWellnessGoal(parameters.wellnessGoal)
                preferences[PreferencesKeys.USE_STATS_PERMISSION] = parameters.useStatsPermission
            }

            cache.saveWeight(weight = parameters.weight)
        }

        /*
         suspend fun saveGender(gender: Gender) = saveParameter(PreferencesKeys.GENDER, GenderStorageConverter.fromGender(gender))

         suspend fun saveBirthDate(birthDate: Long) = saveParameter(PreferencesKeys.BIRTH_DATE, birthDate)

         suspend fun saveWeight(weight: Float) = saveParameter(PreferencesKeys.WEIGHT_KG, weight).also { cacher.saveWeight(weight) }
         */

        suspend fun saveWellnessGoal(goal: WellnessGoal) =
            saveParameter(PreferencesKeys.WELLNESS_GOAL, WellnessGoalStorageConverter.fromWellnessGoal(goal))

        suspend fun saveUseStatsPermission(permission: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.USE_STATS_PERMISSION] = permission
            }
        }

        private suspend fun <T> saveParameter(
            key: Preferences.Key<T>,
            value: T,
        ) = context.dataStore.edit { preferences ->
            preferences[key] = value
        }

        private fun getGender(preferences: Preferences): Gender = PreferencesMapper.toGender(preferences)

        private fun getBirthDate(preferences: Preferences) = PreferencesMapper.toBirthDate(preferences)

        private fun getWeight(preferences: Preferences) = PreferencesMapper.toWeight(preferences)

        private fun getWellnessGoal(preferences: Preferences) = PreferencesMapper.toWellnessGoal(preferences)

        private fun getUseStatsPermission(preferences: Preferences) = PreferencesMapper.toUseStatsPermission(preferences)

        object PreferencesMapper {
            fun toBirthDate(preferences: Preferences): Long {
                val birthDate = preferences[PreferencesKeys.BIRTH_DATE] ?: DefaultUserParams.DEFAULT_BIRTH_DATE
                return birthDate
            }

            fun toWeight(preferences: Preferences) = preferences[PreferencesKeys.WEIGHT_KG] ?: DefaultUserParams.DEFAULT_WEIGHT

            fun toGender(preferences: Preferences): Gender {
                val gender = preferences[PreferencesKeys.GENDER] ?: DefaultUserParams.DEFAULT_GENDER
                return GenderStorageConverter.toGender(gender)
            }

            fun toWellnessGoal(preferences: Preferences): WellnessGoal {
                val goal = preferences[PreferencesKeys.WELLNESS_GOAL] ?: DefaultUserParams.DEFAULT_WELLNESS_GOAL
                return WellnessGoalStorageConverter.toWellnessGoal(goal)
            }

            fun toUseStatsPermission(preferences: Preferences) =
                preferences[PreferencesKeys.USE_STATS_PERMISSION] ?: DefaultUserParams.DEFAULT_USE_STATS_PERMISSION
        }

}
