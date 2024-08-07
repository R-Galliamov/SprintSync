package com.developers.sprintsync.user.ui.userProfile.chart

import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.ui.userProfile.chart.appearance.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataConfigurationFactory
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataHandler
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.ChartNavigator
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener.ChartGestureListener
import com.github.mikephil.charting.charts.CombinedChart

/**
 * A class responsible for rendering a chart with the provided data.
 *
 * @param chart The [CombinedChart] instance to render the data on.
 */
class ChartRenderer(
    private val chart: CombinedChart,
) {
    private val dataHandler = ChartDataHandler()

    private val dataConfigFactory by lazy { ChartDataConfigurationFactory(chart.context) }

    private val configurator: ChartConfigurator by lazy { ChartConfigurator(chart) }

    private val chartConfigFactory by lazy { ChartConfigurationFactory() }

    /**
     * Renders the chart with the provided [WeeklyChartData].
     *
     * @param data The weekly chart data to be displayed.
     */
    fun renderChart(data: WeeklyChartData) {
        val combinedData =
            dataHandler.prepareCombinedData(
                data.data,
                dataConfigFactory.createBarConfiguration(data),
                dataConfigFactory.createLineConfiguration(data),
            )
        chart.data = combinedData

        configurator.configureChart(
            chartConfigFactory.createConfiguration(
                data.data.last().goal,
                data.referenceTimestamp,
                ChartGestureListener(chart, ChartNavigator(data), configurator),
            ),
        )

        refreshChart()
    }

    private fun refreshChart() {
        chart.data?.let {
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }
}
