package com.developers.sprintsync.tracking.analytics.dataManager.calculator

object CaloriesConverter {
    fun kiloCaloriesToCalories(calories: Int): Float = calories * CALORIES_IN_KILOCALORIE

    private const val CALORIES_IN_KILOCALORIE = 1000f
}
