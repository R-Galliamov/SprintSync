package com.developers.sprintsync.core.presentation.view.pace_chart.formatter

import com.developers.sprintsync.core.util.time.TimeConverter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToLong

class PaceChartDurationFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String {
        val roundedValue = value.roundToLong()
        val seconds = TimeConverter.convertFromMillis(roundedValue, TimeConverter.TimeUnit.SECONDS).toInt()

        return if (seconds < SECONDS_BARRIER) {
            getFormattedSeconds(seconds)
        } else {
            val minutes = TimeConverter.convertFromMillis(roundedValue, TimeConverter.TimeUnit.MINUTES).toInt()
            getFormattedMinutes(minutes)
        }
    }

    private fun getFormattedSeconds(seconds: Int): String = "${seconds}s"

    private fun getFormattedMinutes(minutes: Int): String = "${minutes}m"

    companion object {
        private const val SECONDS_BARRIER = 180
    }
}
