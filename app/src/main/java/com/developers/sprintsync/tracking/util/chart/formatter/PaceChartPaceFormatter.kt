package com.developers.sprintsync.tracking.util.chart.formatter

import com.developers.sprintsync.tracking.mapper.indicator.PaceMapper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PaceChartPaceFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String {
        return PaceMapper.formatPaceWithOneDecimal(value)
    }
}
