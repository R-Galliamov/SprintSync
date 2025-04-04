package com.developers.sprintsync.data.user_parameters.converter

import com.developers.sprintsync.domain.user_parameters.model.WellnessGoal

class WellnessGoalStorageConverter {
    companion object {
        fun toWellnessGoal(goal: String): WellnessGoal = WellnessGoal.valueOf(goal)

        fun fromWellnessGoal(goal: WellnessGoal): String = goal.name
    }
}
