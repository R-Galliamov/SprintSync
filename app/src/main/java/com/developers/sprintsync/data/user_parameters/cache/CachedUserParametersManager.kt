package com.developers.sprintsync.data.user_parameters.cache

import android.content.SharedPreferences
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
