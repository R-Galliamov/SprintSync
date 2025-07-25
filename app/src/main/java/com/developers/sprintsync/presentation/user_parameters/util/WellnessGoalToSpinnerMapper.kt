package com.developers.sprintsync.presentation.user_parameters.util

import com.developers.sprintsync.core.util.view.spinner.data.SpinnerItem
import com.developers.sprintsync.core.util.view.spinner.mapper.ItemToSpinnerMapper
import com.developers.sprintsync.domain.user_profile.model.WellnessGoal

class WellnessGoalToSpinnerMapper : ItemToSpinnerMapper<WellnessGoal>() {
    override val itemToSpinnerMap: Map<WellnessGoal, SpinnerItem> = createMap()

    private fun createMap(): Map<WellnessGoal, SpinnerItem> =
        WellnessGoal.entries.associateWith { goal ->
            SpinnerItem(defineDisplayText(goal))
        }

    private fun defineDisplayText(goal: WellnessGoal): String =
        when (goal) {
            WellnessGoal.WEIGHT_LOSS -> WEIGHT_LOSS_DISPLAY_TEXT
            WellnessGoal.MAINTAIN_WEIGHT -> MAINTAIN_WEIGHT_DISPLAY_TEXT
            WellnessGoal.IMPROVE_FITNESS -> IMPROVE_FITNESS_DISPLAY_TEXT
        }

    // TODO use context.getString for translating
    companion object {
        private const val WEIGHT_LOSS_DISPLAY_TEXT = "Weight loss"
        private const val MAINTAIN_WEIGHT_DISPLAY_TEXT = "Maintain weight"
        private const val IMPROVE_FITNESS_DISPLAY_TEXT = "Improve fitness"
    }
}
