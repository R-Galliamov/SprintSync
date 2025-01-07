package com.developers.sprintsync.statistics.domain.chart.formatters.entries

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class DistanceValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DistanceFormatter.metersToPresentableKilometers(value.toInt())
}
