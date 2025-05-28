package com.developers.sprintsync.presentation.user_parameters.util

import com.developers.sprintsync.core.util.view.spinner.data.SpinnerItem
import com.developers.sprintsync.core.util.view.spinner.mapper.ItemToSpinnerMapper
import com.developers.sprintsync.domain.user_parameters.model.Gender

class GenderToSpinnerMapper : ItemToSpinnerMapper<Gender>() {
    override val itemToSpinnerMap: Map<Gender, SpinnerItem> = createMap()

    private fun createMap(): Map<Gender, SpinnerItem> =
        Gender.entries.associateWith { gender ->
            SpinnerItem(defineDisplayText(gender))
        }

    private fun defineDisplayText(gender: Gender): String =
        when (gender) {
            Gender.MALE -> MALE_DISPLAY_TEXT
            Gender.FEMALE -> FEMALE_DISPLAY_TEXT
            Gender.OTHER -> OTHER_DISPLAY_TEXT
        }

    // TODO use context.getString for translating
    companion object {
        private const val MALE_DISPLAY_TEXT = "Male"
        private const val FEMALE_DISPLAY_TEXT = "Female"
        private const val OTHER_DISPLAY_TEXT = "Other"
    }
}
