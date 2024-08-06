package com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter

import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * A [ValueFormatter] that displays only the y-axis value that matches the [displayedValue].
 */
class SelectiveYAxisValueFormatter(
    private val displayedValue: Float?,
) : ValueFormatter() {
    /**
     * Returns the formatted y-axis value. If the value matches [displayedValue], it is returned as a String.
     * Otherwise, an empty String is returned to hide the label.
     *
     * @param value The y-axis value to format.
     * @return The formatted y-axis value or an empty String.
     */
    override fun getFormattedValue(value: Float): String =
        if (value == displayedValue) {
            value.toString()
        } else {
            ""
        }
}
