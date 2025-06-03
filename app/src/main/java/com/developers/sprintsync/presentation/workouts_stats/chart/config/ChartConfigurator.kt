package com.developers.sprintsync.presentation.workouts_stats.chart.config

import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.style_provider.AppThemeProvider
import com.developers.sprintsync.domain.goal.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.axis.YAxisScaler
import com.developers.sprintsync.presentation.workouts_stats.chart.interaction.InteractionConfigurator
import com.developers.sprintsync.presentation.workouts_stats.chart.axis.AxisConfigurator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisConfigurator = AxisConfigurator(chart)

    private val interactionHandler: InteractionConfigurator by lazy { InteractionConfigurator(chart) }

    private val yAxisScaler = YAxisScaler()

    private val colors by lazy { AppThemeProvider(chart.context).Color() }

    private var configuration: ChartConfiguration? = null

    init {
        configureChartDescription()
        axisConfigurator.applyXAxisLabelStyle(R.style.ChartLabel_xAxis)
        axisConfigurator.applyYAxisLabelStyle(R.style.ChartLabel_yAxis)
        axisConfigurator.styleYAxisRightLineColor(colors.onPrimaryVariant)
    }

    fun configureChart(configToBeApplied: ChartConfiguration) {
        configuration = configToBeApplied
        configToBeApplied.let {
            axisConfigurator.setXAxisLimits()
            axisConfigurator.setXValueFormatter(it.xValueFormatter)
            axisConfigurator.setYValueFormatter(it.yValueFormatter)
            configureVisibleRange(it.visibleXRange.toFloat())
            configureInteraction(it.onGestureListener)
        }
    }

    fun scaleYAxisMaximum(maxDataValue: Float) {
        yAxisScaler.scaleUpMaximum(chart, maxDataValue)
    }

    fun animateYAxisScaling(
        maxDataValue: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) {
        yAxisScaler.scaleUpMaximumAnimated(chart, maxDataValue, onAnimationEnd)
    }

    fun highlightYAxisLabel(
        metric: Metric,
        value: Float,
    ) {
        axisConfigurator.highlightYAxisValue(metric, value)
    }

    fun updateChartDisplay() {
        chart.notifyDataSetChanged()
        chart.invalidate()
        axisConfigurator.setXAxisLimits()
        configuration?.let {
            configureVisibleRange(it.visibleXRange.toFloat())
        }
    }

    private fun configureInteraction(onGestureListener: OnChartGestureListener? = null) =
        interactionHandler.configureInteraction(onGestureListener)

    private fun configureChartDescription() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
    }

    private fun configureVisibleRange(visibleRange: Float) {
        chart.setVisibleXRangeMaximum(visibleRange)
        chart.setVisibleXRangeMinimum(visibleRange)
    }

    companion object {
        private const val TAG = "My stack: ChartConfigurator"
    }
}
