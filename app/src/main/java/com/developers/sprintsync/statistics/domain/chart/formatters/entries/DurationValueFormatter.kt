package com.developers.sprintsync.statistics.domain.chart.formatters.entries

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class DurationValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DurationFormatter.formatToMm(value.toLong(), false)
}
