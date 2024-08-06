package com.developers.sprintsync.user.ui.userProfile.util.chart.styling

import com.developers.sprintsync.R
import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.styleProvider.ResourceTextStyleProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisStyler by lazy { ChartAxisStyler(chart) }
    private val barStyleProvider by lazy { ResourceTextStyleProvider(chart.context, R.style.ChartLabel_barLabel) }
    private val colors by lazy { AppThemeManager(chart.context).Color() }

    fun configureChart(
        referenceTimeStamp: Long,
        displayedYAxisValue: Float,
        onGestureListener: OnChartGestureListener? = null,
    ) {
        axisStyler.configureXAxis(referenceTimeStamp, R.style.ChartLabel_xAxis)
        axisStyler.configureYAxis(
            displayedYAxisValue,
            R.style.ChartLabel_yAxis,
            colors.onPrimaryVariant,
        )

        configureInteraction(onGestureListener)
        configureChartDescription()
    }

    fun setAxisLimits(data: CombinedData) {
        setYAxisLimits(data)
        setXAxisLimits(data)
    }

    fun configureVisibleRange() {
        chart.setVisibleXRangeMaximum(MAX_VISIBLE_COUNT.toFloat())
        //chart.moveViewToX(chart.data.xMax)
    }

    fun getBarConfiguration(data: WeeklyChartData) =
        BarConfiguration(
            barColor = colors.secondary,
            barWidth = BAR_WIDTH,
            missingBarColor = colors.secondaryVariant,
            missingBarHeight = calculateMissingBarHeight(data.data),
            barLabelColor = barStyleProvider.textColor,
            barLabelSizeDp = barStyleProvider.textSizeDp,
            balLabelTypeFace = barStyleProvider.typeface,
            dataLabel = data.label,
        )

    fun getLineConfiguration(data: WeeklyChartData) =
        LineConfiguration(
            lineColor = colors.fourthly,
            lineWidth = LINE_WIDTH,
            label = data.label,
            drawValues = false,
            drawCircles = false,
            drawFilled = false,
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER,
        )

    private fun calculateMissingBarHeight(data: List<DailyDataPoint>) = data.minOf { it.goal } * MISSING_BAR_MULTIPLIER

    private fun configureInteraction(onGestureListener: OnChartGestureListener? = null) {
        chart.apply {
            setTouchEnabled(true)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            onGestureListener?.let { onChartGestureListener = it }
        }
    }

    private fun setYAxisLimits(data: CombinedData) {
        (data.yMax * Y_AXIS_MULTIPLIER).let { value ->
            chart.axisLeft.axisMaximum = value
            chart.axisRight.axisMaximum = value
        }
    }

    private fun setXAxisLimits(data: CombinedData) {
        val xAxis: XAxis = chart.xAxis
        xAxis.axisMinimum = data.xMin - X_AXIS_OFFSET
        xAxis.axisMaximum = data.xMax + X_AXIS_OFFSET
    }

    private fun configureChartDescription() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
    }

    companion object {
        private const val MAX_VISIBLE_COUNT = 7
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.2f
        private const val Y_AXIS_MULTIPLIER = 1.25f
        private const val X_AXIS_OFFSET = 0.5f
    }
}
