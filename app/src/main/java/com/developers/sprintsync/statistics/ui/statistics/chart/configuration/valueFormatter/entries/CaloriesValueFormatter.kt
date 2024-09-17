package com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.entries

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class CaloriesValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = CaloriesFormatter.formatCalories(value.toInt())
}
