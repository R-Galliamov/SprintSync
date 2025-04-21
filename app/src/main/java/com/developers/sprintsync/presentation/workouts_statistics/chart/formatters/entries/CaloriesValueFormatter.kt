package com.developers.sprintsync.presentation.workouts_statistics.chart.formatters.entries

import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class CaloriesValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = CaloriesUiFormatter.format(value, CaloriesUiFormatter.Pattern.PLAIN)
}
