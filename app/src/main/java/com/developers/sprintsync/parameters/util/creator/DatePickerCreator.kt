package com.developers.sprintsync.parameters.util.creator

import com.developers.sprintsync.R
import com.developers.sprintsync.global.util.formatter.DateFormatter
import com.google.android.material.datepicker.MaterialDatePicker

class DatePickerCreator {
    fun create(
        selection: Long,
        onPositiveButtonClickListener: (String) -> Unit,
    ) = MaterialDatePicker.Builder.datePicker()
        .setSelection(selection)
        .setTitleText(R.string.select_date)
        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        .build()
        .also { picker ->
            picker.addOnPositiveButtonClickListener { timestamp ->
                onPositiveButtonClickListener(DateFormatter.formatDate(timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR))
            }
        }
}