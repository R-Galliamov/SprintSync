package com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.core.util.log.TimberAppLogger
import com.developers.sprintsync.presentation.components.KilometerFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class DistanceValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String =
        KilometerFormatter(DistanceConverter(TimberAppLogger()), TimberAppLogger()).format(value).value // TODO inject
}
