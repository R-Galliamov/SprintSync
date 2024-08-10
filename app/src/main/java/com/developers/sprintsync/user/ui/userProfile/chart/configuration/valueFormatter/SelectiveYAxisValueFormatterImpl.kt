package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter

class SelectiveYAxisValueFormatterImpl : SelectiveYAxisValueFormatter() {
    private var selectedValue: Float? = null

    override fun selectYAxisValue(value: Float) {
        selectedValue = value
    }

    override fun getFormattedValue(value: Float): String =
        if (value == selectedValue) {
            value.toString()
        } else {
            ""
        }
}
