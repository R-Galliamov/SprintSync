package com.developers.sprintsync.core.presentation.view.pace_chart.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class PaceChartDurationFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String {
        val roundedValue = value.roundToLong()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(roundedValue).toInt()

        return if (seconds < SECONDS_BARRIER) {
            getFormattedSeconds(seconds)
        } else {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(roundedValue).toInt()
            getFormattedMinutes(minutes)
        }
    }

    private fun getFormattedSeconds(seconds: Int): String = "${seconds}s"

    private fun getFormattedMinutes(minutes: Int): String = "${minutes}m"

    companion object {
        private const val SECONDS_BARRIER = 180
    }
}
