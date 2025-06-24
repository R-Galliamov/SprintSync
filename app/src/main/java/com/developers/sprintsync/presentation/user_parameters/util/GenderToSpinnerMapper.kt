package com.developers.sprintsync.presentation.user_parameters.util

import com.developers.sprintsync.core.util.view.spinner.data.SpinnerItem
import com.developers.sprintsync.core.util.view.spinner.mapper.ItemToSpinnerMapper
import com.developers.sprintsync.domain.user_profile.model.Sex

class GenderToSpinnerMapper : ItemToSpinnerMapper<Sex>() {
    override val itemToSpinnerMap: Map<Sex, SpinnerItem> = createMap()

    private fun createMap(): Map<Sex, SpinnerItem> =
        Sex.entries.associateWith { gender ->
            SpinnerItem(defineDisplayText(gender))
        }

    private fun defineDisplayText(sex: Sex): String =
        when (sex) {
            Sex.MALE -> MALE_DISPLAY_TEXT
            Sex.FEMALE -> FEMALE_DISPLAY_TEXT
        }

    // TODO use context.getString for translating
    companion object {
        private const val MALE_DISPLAY_TEXT = "Male"
        private const val FEMALE_DISPLAY_TEXT = "Female"
    }
}
