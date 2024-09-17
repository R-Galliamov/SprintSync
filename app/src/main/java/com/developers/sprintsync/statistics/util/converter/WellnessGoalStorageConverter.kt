package com.developers.sprintsync.statistics.util.converter

import com.developers.sprintsync.statistics.model.goal.WellnessGoal

class WellnessGoalStorageConverter {
    companion object {
        fun toWellnessGoal(goal: String): WellnessGoal = WellnessGoal.valueOf(goal)

        fun fromWellnessGoal(goal: WellnessGoal): String = goal.name
    }
}
