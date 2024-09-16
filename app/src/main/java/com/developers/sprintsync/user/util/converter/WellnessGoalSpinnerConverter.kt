package com.developers.sprintsync.user.util.converter

import com.developers.sprintsync.user.model.goal.WellnessGoal
import com.developers.sprintsync.user.model.ui.SpinnerItem

object WellnessGoalSpinnerConverter {
    fun toSpinnerItem(aim: WellnessGoal): SpinnerItem = SpinnerItem(aim.displayText)
}
