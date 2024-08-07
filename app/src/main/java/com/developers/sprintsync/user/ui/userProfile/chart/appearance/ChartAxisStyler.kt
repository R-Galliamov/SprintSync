package com.developers.sprintsync.user.ui.userProfile.chart.appearance

import com.developers.sprintsync.global.styleProvider.textStyle.ResourceTextStyleProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * A utility class to style the axes of a [CombinedChart].
 *
 * @param chart The [CombinedChart] to style.
 */
class ChartAxisStyler(
    private val chart: CombinedChart,
) {
    /**
     * Configures the X-axis of the chart.
     *
     * @param formatter An optional [ValueFormatter] to format the X-axis labels.
     * @param styleResId An optional resource ID for the text style of the X-axis labels.
     */
    fun configureXAxis(
        formatter: ValueFormatter? = null,
        styleResId: Int? = null,
    ) {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            formatter?.let { valueFormatter = it }
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

    /**
     * Configures the Y-axis of the chart.
     *
     * @param formatter An optional [ValueFormatter] to format the Y-axis labels.
     * @param styleResId An optional resource ID for the text style of the Y-axis labels.
     * @param rightAxisLineColor An optional color for the right axis line.
     */
    fun configureYAxis(
        formatter: ValueFormatter? = null,
        styleResId: Int? = null,
        rightAxisLineColor: Int? = null,
    ) {
        chart.axisLeft.isEnabled = false
        chart.axisRight.apply {
            setDrawAxisLine(true)
            setDrawGridLines(false)
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            formatter?.let { valueFormatter = it }
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
