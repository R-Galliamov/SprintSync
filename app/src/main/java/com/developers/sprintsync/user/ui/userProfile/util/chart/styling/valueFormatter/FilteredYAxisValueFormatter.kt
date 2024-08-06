package com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter

import com.github.mikephil.charting.formatter.ValueFormatter

class FilteredYAxisValueFormatter(
    private val displayedValue: Float?,
) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String =
        if (value == displayedValue) {
            value.toString()
        } else {
            ""
        }
}
