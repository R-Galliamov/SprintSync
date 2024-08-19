package com.developers.sprintsync.user.ui.userProfile.chart.data.transformer

import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataPoints
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData

/**
 * A class responsible for handling and preparing chart data for display.
 */
class ChartDataPreparer {
    /**
     * Prepares combined data for a chart, including both bar and line data.
     *
     * @param data The list of daily data points to be displayed.
     * @param barConfig The configuration for the bar chart.
     * @param lineConfig The configuration for the line chart.
     * @return A [CombinedData] object containing both bar and line data.
     */
    fun prepareCombinedData(
        data: ChartDataPoints,
        barConfig: BarConfiguration,
        lineConfig: LineConfiguration,
    ): CombinedData {
        val combinedData = CombinedData()
        combinedData.setData(prepareBarData(data, barConfig))
        combinedData.setData(prepareLineData(data, lineConfig))
        return combinedData
    }

    /**
     * Prepares bar chart data from the provided daily data points and configuration.*
     * @param data The list of daily data points.
     * @param config The configuration for the bar chart.
     * @return A [BarData] object representing the bar chart data.
     */
    private fun prepareBarData(
        data: ChartDataPoints,
        config: BarConfiguration,
    ): BarData = ChartDataTransformer.barDataBuilder().setConfiguration(config).build(data)

    /**
     * Prepares line chart data from the provided daily data points and configuration.
     *
     * @param data The list of daily data points.
     * @param config The configuration for the line chart.
     * @return A [LineData] object representing the line chart data.
     */
    private fun prepareLineData(
        data: ChartDataPoints,
        config: LineConfiguration,
    ): LineData = ChartDataTransformer.lineDataBuilder().setConfiguration(config).build(data)
}
