package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.entries

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class DistanceValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = DistanceFormatter.metersToPresentableKilometers(value.toInt())
}
