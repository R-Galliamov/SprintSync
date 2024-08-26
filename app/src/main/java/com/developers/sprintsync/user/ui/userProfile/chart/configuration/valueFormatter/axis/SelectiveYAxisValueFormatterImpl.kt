package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.axis

import android.util.Log
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.user.model.chart.chartData.Metric

class SelectiveYAxisValueFormatterImpl : SelectiveYAxisValueFormatter() {
    private var selectedValue: Float? = null

    private var metric: Metric? = null

    override fun selectYAxisValue(
        metric: Metric,
        value: Float?,
    ) {
        this.metric = metric
        selectedValue = value
    }

    override fun getFormattedValue(value: Float): String {
        return if (value == selectedValue) {
            when (metric) {
                Metric.DISTANCE -> DistanceFormatter.metersToPresentableKilometers(value.toInt(), true)
                Metric.DURATION -> DurationFormatter.formatToHhMm(value.toLong())
                else -> ""
            }
        } else {
            ""
        }
    }
}
