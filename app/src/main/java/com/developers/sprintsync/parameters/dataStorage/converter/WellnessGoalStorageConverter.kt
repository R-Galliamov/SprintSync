package com.developers.sprintsync.parameters.dataStorage.converter

import com.developers.sprintsync.statistics.domain.goal.WellnessGoal

class WellnessGoalStorageConverter {
    companion object {
        fun toWellnessGoal(goal: String): WellnessGoal = WellnessGoal.valueOf(goal)

        fun fromWellnessGoal(goal: WellnessGoal): String = goal.name
    }
}
