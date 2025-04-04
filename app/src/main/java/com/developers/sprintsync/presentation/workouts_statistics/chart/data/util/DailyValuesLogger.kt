package com.developers.sprintsync.presentation.workouts_statistics.chart.data.util

import android.util.Log
import com.developers.sprintsync.presentation.workouts_statistics.chart.data.DailyValues

object DailyValuesLogger {
    fun logValues(values: List<DailyValues>?) {
        if (values == null) {
            Log.e("DailyValuesLogger", "Attempted to log null values list")
            throw IllegalArgumentException("Values list cannot be null")
        }
        if (values.isEmpty()) {
            Log.d("DailyValuesLogger", "Empty values list")
            return
        }

        values.forEachIndexed { index, value ->
            val message = when (value) {
                is DailyValues.Present -> """
                    Value #$index (Present):
                    - Goal: ${value.goal}
                    - Actual Value: ${value.actualValue}
                """.trimIndent()
                is DailyValues.Missing -> """
                    Value #$index (Missing):
                    - Goal: ${value.goal}
                """.trimIndent()
            }
            Log.d("DailyValuesLogger", message)
        }
    }
}