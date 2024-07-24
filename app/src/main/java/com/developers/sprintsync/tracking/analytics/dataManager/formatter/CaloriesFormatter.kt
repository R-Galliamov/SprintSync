package com.developers.sprintsync.tracking.analytics.dataManager.formatter

class CaloriesFormatter {
    companion object {
        private const val UNIT = "kcal"

        fun formatCalories(calories: Int): String = "$calories $UNIT"
    }
}
