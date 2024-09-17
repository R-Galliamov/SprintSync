package com.developers.sprintsync.statistics.ui.statistics.chart.configuration.configurator

import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.statistics.ui.statistics.chart.interaction.animation.YAxisScaler
import com.developers.sprintsync.statistics.ui.statistics.chart.interaction.handler.ChartInteractionHandler
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisConfigurator = ChartAxisConfigurator(chart)

    private val interactionHandler: ChartInteractionHandler by lazy { ChartInteractionHandler(chart) }

    private val yAxisScaler = YAxisScaler()

    private val colors by lazy { AppThemeProvider(chart.context).Color() }

    private var configuration: ChartConfiguration? = null

    init {
        configureChartDescription()
        axisConfigurator.styleXAxisLabels(R.style.ChartLabel_xAxis)
        axisConfigurator.styleYAxisLabels(R.style.ChartLabel_yAxis)
        axisConfigurator.styleYAxisRightLine(colors.onPrimaryVariant)
    }

    fun applyConfiguration(configToBeApplied: ChartConfiguration) {
        configuration = configToBeApplied
        configToBeApplied.let {
            axisConfigurator.configureXAxisLimits()
            axisConfigurator.setXValueFormatter(it.xValueFormatter)
            axisConfigurator.setYValueFormatter(it.yValueFormatter)
            configureVisibleRange(it.visibleXRange.toFloat())
            configureInteraction(it.onGestureListener)
        }
    }

    fun scaleUpMaximum(maxDataValue: Float) {
        yAxisScaler.scaleUpMaximum(chart, maxDataValue)
    }

    fun scaleUpMaximumAnimated(
        maxDataValue: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) {
        yAxisScaler.scaleUpMaximumAnimated(chart, maxDataValue, onAnimationEnd)
    }

    fun selectYLabel(
        metric: Metric,
        value: Float,
    ) {
        axisConfigurator.selectYValueLabel(metric, value)
    }

    fun refreshChart() {
        chart.notifyDataSetChanged()
        chart.invalidate()
        axisConfigurator.configureXAxisLimits()
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
