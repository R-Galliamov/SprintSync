package com.developers.sprintsync.user.util.converter

import com.developers.sprintsync.user.model.goal.WellnessGoal

class WellnessGoalStorageConverter {
    companion object {
        fun toWellnessGoal(goal: String): WellnessGoal = WellnessGoal.valueOf(goal)

        fun fromWellnessGoal(goal: WellnessGoal): String = goal.name
    }
}
