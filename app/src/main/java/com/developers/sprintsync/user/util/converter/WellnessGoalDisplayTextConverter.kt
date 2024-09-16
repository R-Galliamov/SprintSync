package com.developers.sprintsync.user.util.converter

import com.developers.sprintsync.user.model.goal.WellnessGoal

object WellnessGoalDisplayTextConverter {
    fun toDisplayText(goal: WellnessGoal): String =
        when (goal) {
            WellnessGoal.WEIGHT_LOSS -> WEIGHT_LOSS_DISPLAY_TEXT
            WellnessGoal.MAINTAIN_WEIGHT -> MAINTAIN_WEIGHT_DISPLAY_TEXT
            WellnessGoal.IMPROVE_FITNESS -> IMPROVE_FITNESS_DISPLAY_TEXT
        }

    // TODO use context.getString for translating
    private const val WEIGHT_LOSS_DISPLAY_TEXT = "Weight loss"
    private const val MAINTAIN_WEIGHT_DISPLAY_TEXT = "Maintain weight"
    private const val IMPROVE_FITNESS_DISPLAY_TEXT = "Improve fitness"
}
