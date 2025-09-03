package com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries

import com.developers.sprintsync.presentation.components.formatter.CaloriesUiFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class CaloriesValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = CaloriesUiFormatter.format(value, CaloriesUiFormatter.Pattern.PLAIN)
}
