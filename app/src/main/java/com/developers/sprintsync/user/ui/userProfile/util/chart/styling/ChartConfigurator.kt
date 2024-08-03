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

class ChartConfigurator(
    private val chart: CombinedChart,
    private val themeManager: AppThemeManager,
) {
    private val axisStyler by lazy { ChartAxisStyler(chart) }
    private val barStyleProvider by lazy { ResourceTextStyleProvider(chart.context, R.style.ChartLabel_barLabel) }

    fun configureChartAppearance(displayedYAxisValue: Float) {
        axisStyler.configureXAxis(R.style.ChartLabel_xAxis)
        axisStyler.configureYAxis(
            displayedYAxisValue,
            R.style.ChartLabel_yAxis,
            themeManager.getOnPrimaryVariantColor(),
        )
        configureInteraction()
        configureChartDescription()
    }

    fun setAxisLimits(data: CombinedData) {
        setYAxisLimits(data)
        setXAxisLimits(data)
    }

    fun getBarConfiguration(data: WeeklyChartData) =
        BarConfiguration(
            barColor = themeManager.getSecondaryColor(),
            barWidth = BAR_WIDTH,
            missingBarColor = themeManager.getSecondaryVariantColor(),
            missingBarHeight = calculateMissingBarHeight(data.data),
            barLabelColor = barStyleProvider.textColor,
            barLabelSizeDp = barStyleProvider.textSizeDp,
            balLabelTypeFace = barStyleProvider.typeface,
            dataLabel = data.label,
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

    private fun configureInteraction() {
        chart.apply {
            setTouchEnabled(true)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            // isDragEnabled = true
            setScaleEnabled(false)
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
        private const val Y_AXIS_MULTIPLIER = 1.1f
        private const val X_AXIS_OFFSET = 0.5f
    }
}
