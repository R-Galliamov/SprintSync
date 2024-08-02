package com.developers.sprintsync.user.ui.userProfile.util.chart

import com.developers.sprintsync.R
import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineDataSet

class WeeklyChartConfigurator(
    private val chart: CombinedChart,
    private val themeManager: AppThemeManager,
) {
    private val axisStyler by lazy { WeeklyChartAxisStyler(chart) }

    init {
        configureChartAppearance()
    }

    fun setAxisLimits(data: CombinedData) {
        setYAxisLimits(data)
        setXAxisLimits(data)
    }

    fun getBarConfiguration(data: WeeklyChartData) =
        BarConfiguration(
            barColor = themeManager.getSecondaryColor(),
            barWidth = BAR_WIDTH,
            missingBarColor = themeManager.getSecondaryColorVariant(),
            missingBarHeight = calculateMissingBarHeight(data.data),
            label = data.label,
        )

    fun getLineConfiguration(data: WeeklyChartData) =
        LineConfiguration(
            lineColor = themeManager.getFourthlyColor(),
            lineWidth = LINE_WIDTH,
            label = data.label,
            drawValues = false,
            drawCircles = false,
            drawFilled = false,
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER,
        )

    private fun calculateMissingBarHeight(data: List<DailyDataPoint>) = data.minOf { it.goal } * MISSING_BAR_MULTIPLIER

    private fun configureChartAppearance() {
        axisStyler.configureXAxis(R.style.SmallText_Bold_Gray)
        axisStyler.configureYAxis()
        configureInteraction()
        configureChartDescription()
    }

    private fun configureInteraction() {
        chart.apply {
            setTouchEnabled(false)
            setPinchZoom(false)
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
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.2f
        private const val Y_AXIS_MULTIPLIER = 1.05f
        private const val X_AXIS_OFFSET = 0.5f
    }
}
