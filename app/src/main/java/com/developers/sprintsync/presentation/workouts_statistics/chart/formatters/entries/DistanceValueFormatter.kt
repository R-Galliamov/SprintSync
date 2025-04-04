package com.developers.sprintsync.presentation.workouts_statistics.chart.formatters.entries

import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.github.mikephil.charting.formatter.ValueFormatter

class DistanceValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DistanceUiFormatter.format(value, DistanceUiPattern.PLAIN)
}
