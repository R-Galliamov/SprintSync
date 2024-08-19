package com.developers.sprintsync.user.ui.userProfile.chart.configuration.configurator

import android.util.Log
import com.developers.sprintsync.global.styleProvider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.SelectiveYAxisValueFormatter
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * A utility class to style the axes of a [CombinedChart].
 *
 * @param chart The [CombinedChart] to style.
 */
class ChartAxisConfigurator(
    private val chart: CombinedChart,
) {
    private val context = chart.context

    private val xAxis = chart.xAxis
    private val yAxisLeft = chart.axisLeft
    private val yAxisRight = chart.axisRight

    private var formatter: SelectiveYAxisValueFormatter? = null

    init {
        configureXAxis()
        configureYAxis()
    }

    fun configureXAxisLimits() {
        xAxis.axisMinimum = chart.data.xMin - X_AXIS_OFFSET
        xAxis.axisMaximum = chart.data.xMax + X_AXIS_OFFSET
    }

    fun styleXAxisLabels(styleResId: Int) {
        val styleProvider = ResourceTextStyleProvider(context, styleResId)
        xAxis.apply {
            textColor = styleProvider.textColor
            typeface = styleProvider.typeface
            textSize = styleProvider.textSizeDp
        }
    }

    fun styleYAxisLabels(styleResId: Int) {
        val styleProvider = ResourceTextStyleProvider(context, styleResId)
        yAxisRight.apply {
            textColor = styleProvider.textColor
            typeface = styleProvider.typeface
            textSize = styleProvider.textSizeDp
        }
    }

    // TODO delete this method
    fun styleYAxisRightLine(color: Int) {
        yAxisRight.apply {
            axisLineColor = color
        }
    }

    fun setXValueFormatter(formatter: ValueFormatter) {
        xAxis.valueFormatter = formatter
    }

    fun setYValueFormatter(formatter: SelectiveYAxisValueFormatter) {
        this.formatter = formatter
        yAxisLeft.valueFormatter = formatter
        yAxisRight.valueFormatter = formatter
    }

    fun selectYValueLabel(value: Float) {
        formatter?.selectYAxisValue(value)
        chart.notifyDataSetChanged()
        chart.invalidate()
        Log.d("My stack: ChartAxisConfigurator", "selectYValueLabel: $value")
    }

    private fun configureXAxis() {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }

    private fun configureYAxis() {
        yAxisLeft.isEnabled = false
        yAxisRight.apply {
            yAxisRight.setLabelCount(Int.MAX_VALUE)
            axisMinimum = Y_AXIS_MINIMUM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            xOffset = X_AXIS_Y_LABEL_OFFSET
            yOffset = X_AXIS_X_LABEL_OFFSET
        }
    }

    companion object {
        private const val Y_AXIS_MINIMUM = 0f
        private const val X_AXIS_OFFSET = 0.5f
        private const val X_AXIS_Y_LABEL_OFFSET = -10f
        private const val X_AXIS_X_LABEL_OFFSET = 10f
    }
}
