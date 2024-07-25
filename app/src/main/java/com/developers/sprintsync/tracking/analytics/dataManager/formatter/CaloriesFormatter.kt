package com.developers.sprintsync.tracking.analytics.dataManager.formatter

class CaloriesFormatter {
    companion object {
        private const val KILOCALORIES_PER_CALORIE = 1000
        private const val UNIT = "kcal"

        fun formatCalories(
            calories: Int,
            includeUnit: Boolean = false,
        ): String {
            val kilocalories = calories / KILOCALORIES_PER_CALORIE
            return if (includeUnit) {
                "$kilocalories $UNIT"
            } else {
                kilocalories.toString()
            }
        }
    }
}
