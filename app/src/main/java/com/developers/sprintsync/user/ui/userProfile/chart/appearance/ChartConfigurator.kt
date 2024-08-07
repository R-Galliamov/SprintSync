package com.developers.sprintsync.user.ui.userProfile.chart.appearance

import android.util.Log
import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.appearance.valueFormatter.SelectiveYAxisValueFormatter
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.ChartInteractionHandler
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.listener.OnChartGestureListener

/**
 * A class to configure the appearance and behavior of a CombinedChart.
 * @param chart The [CombinedChart] to be configured.
 */
class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisStyler by lazy { ChartAxisStyler(chart) }
    private val interactionHandler: ChartInteractionHandler by lazy { ChartInteractionHandler(chart) }

    private val colors by lazy { AppThemeProvider(chart.context).Color() }

    /**
     * Configures the chart based on the provided [ChartConfiguration].
     *
     * @param config The configuration object containing settings for the chart.
     */
    fun configureChart(config: ChartConfiguration) {
        configureXAxisLimits()
        configureVisibleRange()
        configureChartDescription()
        configureInteraction(config.onGestureListener)
        axisStyler.configureXAxis(config.xValueFormatter, R.style.ChartLabel_xAxis)
        axisStyler.configureYAxis(
            config.yValueFormatter,
            R.style.ChartLabel_yAxis,
            colors.onPrimaryVariant,
        )
    }

    /**
     * Sets the limits for the Y-axis, ensuring the maximum value accommodates the highest data point
     * with some extraspace for visual clarity.
     *
     * @param maxDataValue The maximum value present in the data to be displayed on the chart.
     */
    fun setYAxisLimits(maxDataValue: Float) {
        chart.apply {
            Log.d(TAG, "setYAxisLimits: $maxDataValue")
            axisLeft.axisMaximum = maxDataValue * Y_AXIS_HEIGHT_MULTIPLIER
            axisRight.axisMaximum = maxDataValue * Y_AXIS_HEIGHT_MULTIPLIER
        }
    }

    /**
     * Sets the displayed value on the Y-axis using a [SelectiveYAxisValueFormatter].
     *
     * @param value The value to be displayed.
     */
    fun setYaxisDisplayedValue(value: Float) {
        axisStyler.configureYAxis(SelectiveYAxisValueFormatter(value))
    }

    /**
     * Refreshes the chart, causing it to redraw.
     */
    fun refreshChart() {
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    private fun configureInteraction(onGestureListener: OnChartGestureListener? = null) =
        interactionHandler.configureInteraction(onGestureListener)

    private fun configureChartDescription() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
    }

    private fun configureVisibleRange() {
        chart.setVisibleXRangeMaximum(VISIBLE_RANGE.toFloat())
        chart.setVisibleXRangeMinimum(VISIBLE_RANGE.toFloat())
    }

    private fun configureXAxisLimits() {
        val xAxis: XAxis = chart.xAxis
        xAxis.axisMinimum = chart.xChartMin - X_AXIS_OFFSET
        xAxis.axisMaximum = chart.xChartMax + X_AXIS_OFFSET
    }

    companion object {
        private const val VISIBLE_RANGE = 7
        private const val X_AXIS_OFFSET = 0.5f
        private const val Y_AXIS_HEIGHT_MULTIPLIER = 1.25f

        private const val TAG = "My stack: ChartConfigurator"
    }
}
