package com.developers.sprintsync.user.ui.userProfile.chart.configuration

import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.user.model.chart.configuration.ChartConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.ChartInteractionHandler
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.YAxisScaler
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartConfigurator(
    private val chart: CombinedChart,
) {
    private val axisConfigurator = ChartAxisConfigurator(chart)

    private val interactionHandler: ChartInteractionHandler by lazy { ChartInteractionHandler(chart) }

    private val yAxisScaler = YAxisScaler()

    private val colors by lazy { AppThemeProvider(chart.context).Color() }

    private var configToBeApplied: ChartConfiguration? = null

    init {
        configureChartDescription()
        axisConfigurator.styleXAxisLabels(R.style.ChartLabel_xAxis)
        axisConfigurator.styleYAxisLabels(R.style.ChartLabel_yAxis)
        axisConfigurator.styleYAxisRightLine(colors.onPrimaryVariant)
    }

    fun presetConfig(config: ChartConfiguration) {
        this.configToBeApplied = config
    }

    fun applyConfiguration() {
        configToBeApplied?.let {
            axisConfigurator.configureXAxisLimits()
            axisConfigurator.setXValueFormatter(it.xValueFormatter)
            axisConfigurator.setYValueFormatter(it.yValueFormatter)
            configureVisibleRange(it.visibleXRange.toFloat())
            configureInteraction(it.onGestureListener)
        }
        resetConfig()
    }

    fun scaleUpMaximum(
        maxDataValue: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) {
        yAxisScaler.scaleUpMaximum(chart, maxDataValue, onAnimationEnd)
    }

    fun selectYLabel(value: Float) {
        axisConfigurator.selectYValueLabel(value)
        refreshChart()
    }

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

    private fun configureVisibleRange(visibleRange: Float) {
        chart.setVisibleXRangeMaximum(visibleRange)
        chart.setVisibleXRangeMinimum(visibleRange)
    }

    private fun resetConfig() {
        configToBeApplied = null
    }

    companion object {
        private const val TAG = "My stack: ChartConfigurator"
    }
}
