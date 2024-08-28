package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.axis

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
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

    override fun getFormattedValue(value: Float): String =
        if (value == selectedValue) {
            when (metric) {
                Metric.DURATION -> DurationFormatter.formatToMm(value.toLong(), true)
                Metric.DISTANCE -> DistanceFormatter.metersToPresentableKilometers(value.toInt(), true)
                Metric.CALORIES -> CaloriesFormatter.formatCalories(value.toInt(), true)
                null -> ""
            }
        } else {
            ""
        }
}
