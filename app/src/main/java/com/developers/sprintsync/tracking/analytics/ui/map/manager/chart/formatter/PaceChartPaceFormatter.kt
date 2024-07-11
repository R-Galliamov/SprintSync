package com.developers.sprintsync.tracking.analytics.ui.map.manager.chart.formatter

import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.PaceMapper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PaceChartPaceFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String = PaceMapper.formatPaceWithOneDecimal(value)
}
