package com.developers.sprintsync.user.ui.userProfile.util.chart

import com.developers.sprintsync.user.ui.userProfile.util.chart.formatter.WeeklyChartDayFormatter
import com.developers.sprintsync.user.ui.userProfile.util.chart.styleProvider.ResourceChartAxisStyleProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis

class WeeklyChartAxisStyler(
    private val chart: CombinedChart,
) {
    fun configureXAxis(styleResId: Int? = null) {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            valueFormatter = WeeklyChartDayFormatter()
        }

        styleResId?.let {
            val styleProvider = ResourceChartAxisStyleProvider(chart.context, styleResId)
            chart.xAxis.apply {
                textColor = styleProvider.textColor
                typeface = styleProvider.typeface
            }
        }
    }

    fun configureYAxis() {
        chart.axisLeft.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
            // valueFormatter = ... if needed
        }
        chart.axisRight.isEnabled = false
    }
}
