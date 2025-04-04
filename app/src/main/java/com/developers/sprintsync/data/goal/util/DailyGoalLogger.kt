package com.developers.sprintsync.data.goal.util

import android.util.Log
import com.developers.sprintsync.domain.goal.model.DailyGoal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DailyGoalLogger {
    fun logGoals(goals: List<DailyGoal>?) {
        if (goals == null) {
            Log.e("DailyGoalLogger", "Attempted to log null goals list")
            throw IllegalArgumentException("Goals list cannot be null")
        }
        if (goals.isEmpty()) {
            Log.d("DailyGoalLogger", "Empty goals list")
            return
        }

        goals.forEachIndexed { index, goal ->
            val timestampStr =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(goal.timestamp))
            val message =
                """
                Goal #$index:
                - ID: ${goal.id}
                - Timestamp: $timestampStr
                - Metric Type: ${goal.metricType}
                - Value: ${goal.value}
                """.trimIndent()
            Log.d("DailyGoalLogger", message)
        }
    }
}
