package com.developers.sprintsync.user.ui.userProfile.util.chart

import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.ChartConfigurator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData

class ChartRenderer(
    private val chart: CombinedChart,
) {
    private val configurator: ChartConfigurator by lazy {
        ChartConfigurator(
            chart,
            AppThemeManager(chart.context),
        )
    }

    fun renderData(data: WeeklyChartData) {
        val barData = createBarData(data)
        val lineData = createLineData(data)
        val combinedData = createCombinedData(barData, lineData)
        configurator.configureChartAppearance(data.data.last().goal)
        configurator.setAxisLimits(combinedData)
        chart.data = combinedData
        refreshChart()
    }

    private fun refreshChart() {
        chart.data?.let {
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }

    private fun createBarData(data: WeeklyChartData): BarData =
        ChartDataTransformer
            .barDataBuilder()
            .configuration(configurator.getBarConfiguration(data))
            .build(data.data)

    private fun createLineData(data: WeeklyChartData): LineData =
        ChartDataTransformer
            .lineDataBuilder()
            .configuration(configurator.getLineConfiguration(data))
            .build(data.data)

    private fun createCombinedData(
        barData: BarData,
        lineData: LineData,
    ): CombinedData =
        CombinedData().apply {
            setData(barData)
            setData(lineData)
        }
}
