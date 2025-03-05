package com.developers.sprintsync.statistics.domain.chart.formatters.entries

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.CaloriesFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class CaloriesValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = CaloriesFormatter.formatCalories(value)
}
