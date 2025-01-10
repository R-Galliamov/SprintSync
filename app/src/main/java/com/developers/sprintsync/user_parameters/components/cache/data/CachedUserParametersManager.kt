package com.developers.sprintsync.user_parameters.components.cache.data

import android.content.SharedPreferences
import com.developers.sprintsync.user_parameters.components.cache.data.model.KeyConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachedUserParametersManager
    @Inject
    constructor(
        private val cachedParamsSharedPrefs: SharedPreferences,
    ) {
        fun saveWeight(weight: Float) {
            cachedParamsSharedPrefs.edit().putFloat(KeyConstants.USER_WEIGHT_KEY, weight).apply()
        }
    }
