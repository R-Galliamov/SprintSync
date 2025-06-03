package com.developers.sprintsync.presentation.workouts_stats.chart.axis

import com.developers.sprintsync.core.util.style_provider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis.SelectiveYAxisValueFormatter
import com.developers.sprintsync.domain.goal.model.Metric
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * A utility class to style the axes of a [CombinedChart].
 *
 * @param chart The [CombinedChart] to style.
 */
class AxisConfigurator(
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

    fun setXAxisLimits() {
        xAxis.axisMinimum = chart.data.xMin - X_AXIS_OFFSET
        xAxis.axisMaximum = chart.data.xMax + X_AXIS_OFFSET
    }

    fun applyXAxisLabelStyle(styleResId: Int) {
        val styleProvider = ResourceTextStyleProvider(context, styleResId)
        xAxis.apply {
            textColor = styleProvider.textColor
            typeface = styleProvider.typeface
            textSize = styleProvider.textSizeDp
        }
    }

    fun applyYAxisLabelStyle(styleResId: Int) {
        val styleProvider = ResourceTextStyleProvider(context, styleResId)
        yAxisRight.apply {
            textColor = styleProvider.textColor
            typeface = styleProvider.typeface
            textSize = styleProvider.textSizeDp
        }
    }

    fun styleYAxisRightLineColor(color: Int) {
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

    fun highlightYAxisValue(
        metric: Metric,
        value: Float,
    ) {
        formatter?.highlightYAxisValue(metric, value, chart.axisLeft.axisMaximum, Y_AXIS_LABEL_COUNT)
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
            yAxisRight.setLabelCount(Y_AXIS_LABEL_COUNT, true)
            axisMinimum = Y_AXIS_MINIMUM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            xOffset = Y_AXIS_X_LABEL_OFFSET
            yOffset = Y_AXIS_Y_LABEL_OFFSET
        }
    }

    companion object {
        private const val Y_AXIS_MINIMUM = 0f
        private const val X_AXIS_OFFSET = 0.5f
        private const val Y_AXIS_X_LABEL_OFFSET = -10f
        private const val Y_AXIS_Y_LABEL_OFFSET = 10f

        private const val Y_AXIS_LABEL_COUNT = 25

        private const val TAG = "My stack: ChartAxisConfigurator"
    }
}
