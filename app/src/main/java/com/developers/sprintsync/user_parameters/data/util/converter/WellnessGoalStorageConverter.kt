package com.developers.sprintsync.user_parameters.data.util.converter

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal

class WellnessGoalStorageConverter {
    companion object {
        fun toWellnessGoal(goal: String): WellnessGoal = WellnessGoal.valueOf(goal)

        fun fromWellnessGoal(goal: WellnessGoal): String = goal.name
    }
}
