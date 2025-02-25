package com.developers.sprintsync.core.presentation.view.pace_chart

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.style_provider.AppThemeProvider
import com.developers.sprintsync.core.presentation.view.pace_chart.formatter.PaceChartDurationFormatter
import com.developers.sprintsync.core.presentation.view.pace_chart.formatter.PaceChartPaceFormatter
import com.developers.sprintsync.core.presentation.view.pace_chart.model.PaceChartData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class PaceChartManager(
    private val context: Context,
) {
    private var _chart: LineChart? = null
    private val chart get() = requireNotNull(_chart) { context.getString(R.string.chart_is_not_initialized) }

    private val colors = AppThemeProvider(context).Color()

    fun initialize(chart: LineChart) {
        this@PaceChartManager._chart = chart
        setChartStyle()
    }

    fun setData(chartData: PaceChartData) {
        val lineDataSets = chartData.data.map { getLineDataSet(it) }
        setYAxisLimits(chartData.maxPace, chartData.minPace)
        chart.data = LineData(lineDataSets)
        refreshChart()
    }

    fun cleanup() {
        _chart = null
    }

    private fun refreshChart() {
        chart.data?.let {
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }

    private fun getLineDataSet(entries: List<Entry>): LineDataSet =
        LineDataSet(entries, CHART_DATA_LABEL).apply {
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(false)
            lineWidth = LINE_WIDTH
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = this@PaceChartManager.colors.fourthly
        }

    private fun setInteraction() {
        chart.apply {
            setTouchEnabled(false)
            setPinchZoom(false)
        }
    }

    private fun setChartStyle() {
        setInteraction()
        setXAxisStyle()
        setYAxisStyle()
        setChartDescription()
    }

    private fun setYAxisLimits(
        maxValue: Float,
        minValue: Float,
    ) {
        setYAxisMax(maxValue)
        setYAxisMin(minValue)
    }

    private fun setYAxisMax(value: Float) {
        chart.axisLeft.axisMaximum = value + Y_AXIS_OFFSET
    }

    private fun setYAxisMin(value: Float) {
        chart.axisLeft.axisMinimum = maxOf(Y_AXIS_MIN, value - Y_AXIS_OFFSET)
    }

    private fun setXAxisStyle() {
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
            valueFormatter = PaceChartDurationFormatter()
        }
    }

    private fun setYAxisStyle() {
        chart.axisLeft.apply {
            setDrawAxisLine(false)
            setDrawGridLines(true)
            valueFormatter = PaceChartPaceFormatter()
            isInverted = true
        }
        chart.axisRight.isEnabled = false
    }

    private fun setChartDescription() {
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
    }

    companion object {
        private const val CHART_DATA_LABEL = "Pace"
        private const val Y_AXIS_MIN = 0f
        private const val Y_AXIS_OFFSET = 1f
        private const val LINE_WIDTH = 2f
    }
}
