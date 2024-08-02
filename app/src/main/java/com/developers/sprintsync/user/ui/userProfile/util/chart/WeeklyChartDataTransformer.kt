package com.developers.sprintsync.user.ui.userProfile.util.chart

import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class WeeklyChartDataTransformer {
    class BarDataBuilder {
        private var config: BarConfiguration = BarConfiguration.EMPTY_CONFIGURATION

        fun configuration(configuration: BarConfiguration) = apply { this.config = configuration }

        /*
        fun presentColor(color: Int) = apply { config = config.copy(barColor = color) }

        fun missingColor(color: Int) = apply { config = config.copy(missingBarColor = color) }

        fun missingBarHeight(height: Float) = apply { config = config.copy(missingBarHeight = height) }

        fun barWidth(width: Float) = apply { config = config.copy(barWidth = width) }

        fun label(label: String) = apply { config = config.copy(label = label) }

         */

        fun build(data: List<DailyDataPoint>): BarData {
            val presentDataSet = transformToPresentDataSet(data.filterIsInstance<DailyDataPoint.Present>(), config)
            val missingDataSet = transformToMissingDataSet(data.filterIsInstance<DailyDataPoint.Missing>(), config)
            return BarData(presentDataSet, missingDataSet).apply {
                config.barWidth?.let { barWidth = it }
            }
        }

        private fun transformToPresentDataSet(
            data: List<DailyDataPoint.Present>,
            config: BarConfiguration,
        ): BarDataSet {
            val entries = transformToPresentEntries(data)
            return BarDataSet(entries, config.label).apply {
                config.barColor?.let { color = it }
            }
        }

        private fun transformToMissingDataSet(
            data: List<DailyDataPoint.Missing>,
            config: BarConfiguration,
        ): BarDataSet {
            val entries = transformToMissingEntries(data, config.missingBarHeight)
            return BarDataSet(entries, config.label).apply {
                config.missingBarColor?.let { color = it }
            }
        }

        private fun transformToPresentEntries(data: List<DailyDataPoint.Present>): List<BarEntry> =
            data.map { BarEntry(it.weekDay.index.toFloat(), it.value) }

        private fun transformToMissingEntries(
            data: List<DailyDataPoint.Missing>,
            missingBarHeight: Float,
        ): List<BarEntry> = data.map { BarEntry(it.weekDay.index.toFloat(), missingBarHeight) }
    }

    class LineDataBuilder {
        private var config: LineConfiguration = LineConfiguration.EMPTY_CONFIGURATION

        fun configuration(configuration: LineConfiguration) = apply { this.config = configuration }

        /*
        fun color(color: Int) = apply { config = config.copy(lineColor = color) }

        fun width(width: Float) = apply { config = config.copy(width = width) }

        fun label(label: String) = apply { config = config.copy(label = label) }

        fun drawValues(drawValues: Boolean) = apply { config = config.copy(drawValues = drawValues) }

        fun drawCircles(drawCircles: Boolean) = apply { config = config.copy(drawCircles = drawCircles) }

        fun drawFilled(drawFilled: Boolean) = apply { config = config.copy(drawFilled = drawFilled) }

         */

        fun build(data: List<DailyDataPoint>): LineData = LineData(transformToLineDataSet(data, config))

        private fun transformToLineDataSet(
            data: List<DailyDataPoint>,
            config: LineConfiguration,
        ): LineDataSet {
            val entries = transformToLineEntries(data)
            return LineDataSet(entries, config.label).apply {
                config.drawValues?.let { setDrawValues(it) }
                config.drawCircles?.let { setDrawCircles(it) }
                config.drawFilled?.let { setDrawFilled(it) }
                config.lineWidth?.let { lineWidth = it }
                config.lineColor?.let { color = it }
                config.mode?.let { mode = it }
            }
        }

        private fun transformToLineEntries(data: List<DailyDataPoint>): List<BarEntry> =
            data.map { BarEntry(it.weekDay.index.toFloat(), it.goal) }
    }

    companion object {
        fun barDataBuilder() = BarDataBuilder()

        fun lineDataBuilder() = LineDataBuilder()
    }
}
