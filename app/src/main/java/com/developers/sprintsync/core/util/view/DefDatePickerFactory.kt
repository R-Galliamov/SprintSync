package com.developers.sprintsync.core.util.view

import com.developers.sprintsync.R
import com.google.android.material.datepicker.MaterialDatePicker
import javax.inject.Inject

fun interface DatePickerFactory {
    fun create(selection: Long?): MaterialDatePicker<Long>
}

class DefDatePickerFactory @Inject constructor() : DatePickerFactory {
    override fun create(
        selection: Long?,
    ) = MaterialDatePicker.Builder.datePicker()
        .setSelection(selection)
        .setTitleText(R.string.action_select_date)
        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        .build()
}