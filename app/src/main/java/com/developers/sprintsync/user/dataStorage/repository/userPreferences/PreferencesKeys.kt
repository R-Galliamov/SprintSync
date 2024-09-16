package com.developers.sprintsync.user.dataStorage.repository.userPreferences

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val GENDER = stringPreferencesKey("gender")
    val WEIGHT = floatPreferencesKey("weight")
    val WELLNESS_GOAL = stringPreferencesKey("wellness_goal")
}
