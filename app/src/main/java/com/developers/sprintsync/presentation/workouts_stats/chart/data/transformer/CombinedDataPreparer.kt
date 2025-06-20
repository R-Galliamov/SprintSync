package com.developers.sprintsync.presentation.workouts_stats.chart.data.transformer

import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.presentation.workouts_stats.chart.config.BarConfiguration
import com.developers.sprintsync.presentation.workouts_stats.chart.config.LineConfiguration
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData

/**
 * A class responsible for handling and preparing chart data for display.
 */
class CombinedDataPreparer {
    /**
     * Prepares combined data for a chart, including both bar and line data.
     *
     * @param data The list of daily data points to be displayed.
     * @param barConfig The configuration for the bar chart.
     * @param lineConfig The configuration for the line chart.
     * @return A [CombinedData] object containing both bar and line data.
     */
    fun prepareCombinedData(
        data: List<DailyValues>,
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
        data: List<DailyValues>,
        config: BarConfiguration,
    ): BarData = DataTransformer.barDataBuilder().setConfiguration(config).build(data)

    /**
     * Prepares line chart data from the provided daily data points and configuration.
     *
     * @param data The list of daily data points.
     * @param config The configuration for the line chart.
     * @return A [LineData] object representing the line chart data.
     */
    private fun prepareLineData(
        data: List<DailyValues>,
        config: LineConfiguration,
    ): LineData = DataTransformer.lineDataBuilder().setConfiguration(config).build(data)
}
