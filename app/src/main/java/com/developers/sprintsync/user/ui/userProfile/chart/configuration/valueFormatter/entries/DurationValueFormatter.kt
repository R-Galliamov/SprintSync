package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.entries

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class DurationValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DurationFormatter.formatToMm(value.toLong(), false)
}
