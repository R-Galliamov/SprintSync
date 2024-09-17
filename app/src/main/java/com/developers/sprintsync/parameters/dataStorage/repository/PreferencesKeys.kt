package com.developers.sprintsync.parameters.dataStorage.repository

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val GENDER = stringPreferencesKey("gender")
    val WEIGHT = floatPreferencesKey("weight")
    val WELLNESS_GOAL = stringPreferencesKey("wellness_goal")
}
