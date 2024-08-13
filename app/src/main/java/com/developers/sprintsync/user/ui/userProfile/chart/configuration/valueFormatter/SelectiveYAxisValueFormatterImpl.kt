package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter

import android.util.Log

class SelectiveYAxisValueFormatterImpl : SelectiveYAxisValueFormatter() {
    private var selectedValue: Float? = null

    override fun selectYAxisValue(value: Float?) {
        selectedValue = value
    }

    override fun getFormattedValue(value: Float): String {
        Log.d("My stack: SelectiveYAxisValueFormatterImpl", "getFormattedValue: $value")
        return if (value == selectedValue) {
            value.toInt().toString()
        } else {
            ""
        }
    }
}
