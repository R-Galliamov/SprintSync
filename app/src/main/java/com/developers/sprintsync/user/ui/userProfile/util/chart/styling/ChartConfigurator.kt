package com.developers.sprintsync.user.ui.userProfile.util.chart.styling

import android.util.Log
import com.developers.sprintsync.R
import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.util.chart.newChart.ChartInteractionHandler
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisStyler by lazy { ChartAxisStyler(chart) }
    private val interactionHandler: ChartInteractionHandler by lazy { ChartInteractionHandler(chart) }

    private val colors by lazy { AppThemeManager(chart.context).Color() }

    fun configureChart(config: ChartConfiguration) {
        configureInteraction(config.onGestureListener)
        configureChartDescription()
       setAxisLimits()
        configureVisibleRange()
        axisStyler.configureXAxis(config.xValueFormatter, R.style.ChartLabel_xAxis)
        axisStyler.configureYAxis(
            config.yValueFormatter,
            R.style.ChartLabel_yAxis,
            colors.onPrimaryVariant,
        )
    }

    private fun configureInteraction(onGestureListener: OnChartGestureListener? = null) =
        interactionHandler.configureInteraction(onGestureListener)

    private fun configureChartDescription() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
    }

    private fun configureVisibleRange() {
        chart.setVisibleXRangeMaximum(MAX_VISIBLE_COUNT.toFloat())
    }

    private fun setAxisLimits() {
        setYAxisLimits()
        setXAxisLimits()
    }

    private fun setYAxisLimits() {
        (chart.yMax * Y_AXIS_HEIGHT_MULTIPLIER).let { value ->
            chart.axisLeft.axisMaximum = value
            chart.axisRight.axisMaximum = value
        }
    }

    private fun setXAxisLimits() {
        // val xAxis: XAxis = chart.xAxis
        // Log.d("ChartConfigurator", "XAxisMin: ${chart.xChartMin}")
        // Log.d("ChartConfigurator", "XAxisMax: ${chart.xChartMax}")
        // xAxis.axisMinimum = chart.xChartMin - X_AXIS_OFFSET
        // xAxis.axisMaximum = chart.xChartMax + X_AXIS_OFFSET
        // Log.d("ChartConfigurator", "XAxisMin: ${xAxis.axisMinimum}")
        // Log.d("ChartConfigurator", "XAxisMax: ${xAxis.axisMaximum}")
    }

    companion object {
        private const val MAX_VISIBLE_COUNT = 7
        private const val X_AXIS_OFFSET = 0.5f
        private const val Y_AXIS_HEIGHT_MULTIPLIER = 1.25f
    }
}
