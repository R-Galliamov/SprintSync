package com.developers.sprintsync.user.ui.userProfile.util.chart.styling

import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter.ChartDayFormatter
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.valueFormatter.FilteredYAxisValueFormatter
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.styleProvider.ResourceTextStyleProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis

class ChartAxisStyler(
    private val chart: CombinedChart,
) {
    fun configureXAxis(referenceTimeStamp : Long, styleResId: Int? = null) {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            valueFormatter = ChartDayFormatter(referenceTimeStamp)
        }

        styleResId?.let {
            val styleProvider = ResourceTextStyleProvider(chart.context, styleResId)
            chart.xAxis.apply {
                textColor = styleProvider.textColor
                typeface = styleProvider.typeface
                textSize = styleProvider.textSizeDp
            }
        }
    }

    fun configureYAxis(
        displayedValue: Float,
        styleResId: Int? = null,
        rightAxisLineColor: Int? = null,
    ) {
        chart.axisLeft.isEnabled = false
        chart.axisRight.apply {
            setDrawAxisLine(true)
            setDrawGridLines(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            valueFormatter = FilteredYAxisValueFormatter(displayedValue)
            rightAxisLineColor?.let {
                axisLineColor = it
            }
        }

        styleResId?.let {
            val styleProvider = ResourceTextStyleProvider(chart.context, styleResId)
            chart.axisRight.apply {
                textColor = styleProvider.textColor
                typeface = styleProvider.typeface
                textSize = styleProvider.textSizeDp
            }
        }
    }
}
