package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import android.util.Log
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.ui.userProfile.util.chart.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.ChartConfigurator
import com.github.mikephil.charting.charts.CombinedChart

class ChartRenderer(
    private val chart: CombinedChart,
) {
    private val dataHandler = ChartDataHandler()

    private val dataConfigFactory by lazy { ChartDataConfigurationFactory(chart.context) }

    private val configurator: ChartConfigurator by lazy { ChartConfigurator(chart) }

    private val chartConfigFactory by lazy { ChartConfigurationFactory() }

    fun renderChart(data: WeeklyChartData) {
        val combinedData =
            dataHandler.prepareCombinedData(
                data.data,
                dataConfigFactory.createBarConfiguration(data),
                dataConfigFactory.createLineConfiguration(data),
            )
        chart.data = combinedData

        Log.d("Chart", "xMax = ${combinedData.xMax}")
        Log.d("Chart", "xMin = ${combinedData.xMin}")

        configurator.configureChart(
            chartConfigFactory.createConfiguration(
                data.data.last().goal,
                data.referenceTimestamp,
                ChartGestureListener(chart),
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
