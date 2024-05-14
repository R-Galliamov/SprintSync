package com.developers.sprintsync.tracking.util.chart.formatter

import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class PaceChartDurationFormatter : ValueFormatter() {
    private val timeMapper = TimeMapper

    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String {
        val roundedValue = value.roundToLong()
        val seconds = timeMapper.millisToSeconds(roundedValue)

        return if (seconds < SECONDS_BARRIER) {
            getPresentableSeconds(seconds)
        } else {
            val minutes = timeMapper.millisToMinutes(roundedValue)
            getPresentableMinutes(minutes)
        }
    }

    private fun getPresentableSeconds(seconds: Float): String {
        return "${seconds.roundToInt()}s"
    }

    private fun getPresentableMinutes(minutes: Float): String {
        return "${minutes.roundToInt()}m"
    }

    companion object {
        private const val SECONDS_BARRIER = 60
    }
}
