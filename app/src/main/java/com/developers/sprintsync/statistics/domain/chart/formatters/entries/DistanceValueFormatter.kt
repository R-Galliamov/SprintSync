package com.developers.sprintsync.statistics.domain.chart.formatters.entries

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.github.mikephil.charting.formatter.ValueFormatter

class DistanceValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DistanceUiFormatter.format(value, DistanceUiPattern.PLAIN)
}
