package com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries

import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.github.mikephil.charting.formatter.ValueFormatter

class DurationValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DurationUiFormatter.format(value.toLong(), DurationUiPattern.MM)
}
