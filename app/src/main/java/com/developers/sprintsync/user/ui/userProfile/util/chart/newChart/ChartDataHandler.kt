package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData

class ChartDataHandler {
    fun prepareCombinedData(
        data: List<DailyDataPoint>,
        barConfig: BarConfiguration,
        lineConfig: LineConfiguration,
    ): CombinedData {
        val combinedData = CombinedData()
        combinedData.setData(prepareBarData(data, barConfig))
        combinedData.setData(prepareLineData(data, lineConfig))
        return combinedData
    }

    private fun prepareBarData(
        data: List<DailyDataPoint>,
        config: BarConfiguration,
    ): BarData = ChartDataTransformer.barDataBuilder().setConfiguration(config).build(data)

    private fun prepareLineData(
        data: List<DailyDataPoint>,
        config: LineConfiguration,
    ): LineData = ChartDataTransformer.lineDataBuilder().setConfiguration(config).build(data)
}
