package com.developers.sprintsync.statistics.domain.chart.data.transformer

import com.developers.sprintsync.statistics.domain.chart.data.DailyValues
import com.developers.sprintsync.statistics.domain.chart.config.BarConfiguration
import com.developers.sprintsync.statistics.domain.chart.config.LineConfiguration
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * A class responsible for transforming chart data into formats suitable for display using MPAndroidChart library.
 */
class DataTransformer {
    /**
     * A builder class for creating [BarData] objects.
     */
    class BarDataBuilder {
        private var config: BarConfiguration = BarConfiguration.EMPTY_CONFIGURATION

        /**
         * Sets the configuration for the bar chart.
         *
         * @param configuration The [BarConfiguration] to apply.
         * @return The builder instance for chaining.
         */
        fun setConfiguration(configuration: BarConfiguration) = apply { this.config = configuration }

        /*
        fun presentColor(color: Int) = apply { config = config.copy(barColor = color) }

        fun missingColor(color: Int) = apply { config = config.copy(missingBarColor = color) }

        fun missingBarHeight(height: Float) = apply { config = config.copy(missingBarHeight = height) }

        fun barWidth(width: Float) = apply { config = config.copy(barWidth = width) }

        fun label(label: String) = apply { config = config.copy(label = label) }

         */

        fun build(data: List<DailyValues>): BarData {
            val presentDataSet = transformToPresentDataSet(data, config)
            val missingDataSet = transformToMissingDataSet(data, config)
            return BarData(presentDataSet, missingDataSet).apply {
                config.barWidth?.let { barWidth = it }
            }
        }

        private fun transformToPresentDataSet(
            data: List<DailyValues>,
            config: BarConfiguration,
        ): BarDataSet {
            val presentDaysMap = transformToPresentDaysMap(data)
            val entries = transformToPresentEntries(presentDaysMap)
            return BarDataSet(entries, null).apply {
                config.barColor?.let { color = it }
                config.barLabelColor?.let { valueTextColor = it }
                config.balLabelTypeFace?.let { valueTypeface = it }
                config.barLabelSizeDp?.let { valueTextSize = it }
                config.valueFormatter?.let { valueFormatter = it }
                isHighlightEnabled = false
            }
        }

        private fun transformToPresentDaysMap(data: List<DailyValues>): Map<Int, DailyValues.Present> {
            val presentDaysMap = mutableMapOf<Int, DailyValues.Present>()
            data.forEachIndexed { index, dailyValues ->
                if (dailyValues is DailyValues.Present) {
                    presentDaysMap[index] = dailyValues
                }
            }
            return presentDaysMap
        }

        private fun transformToMissingDaysIndices(data: List<DailyValues>): Set<Int> =
            data.indices.filter { data[it] is DailyValues.Missing }.toSet()

        private fun transformToMissingDataSet(
            data: List<DailyValues>,
            config: BarConfiguration,
        ): BarDataSet {
            val missingDaysIndices = transformToMissingDaysIndices(data)
            val entries = transformToMissingEntries(missingDaysIndices, config.missingBarHeight)
            return BarDataSet(entries, null).apply {
                config.missingBarColor?.let { color = it }
                setDrawValues(false)
                isHighlightEnabled = false
            }
        }

        private fun transformToPresentEntries(data: Map<Int, DailyValues.Present>): List<BarEntry> =
            data.map { (index, dailyValues) -> BarEntry(index.toFloat(), dailyValues.actualValue) }

        private fun transformToMissingEntries(
            indexes: Set<Int>,
            missingBarHeight: Float,
        ) = indexes.map { index -> BarEntry(index.toFloat(), missingBarHeight) }
    }

    /**
     * A builder class for creating [LineData] objects.
     */
    class LineDataBuilder {
        private var config: LineConfiguration = LineConfiguration.EMPTY_CONFIGURATION

        /**
         * Sets the configuration for the line chart.
         * @param configuration The [LineConfiguration] to apply.
         * @return The builder instance for chaining.
         */
        fun setConfiguration(configuration: LineConfiguration) = apply { this.config = configuration }

        /*
        fun color(color: Int) = apply { config = config.copy(lineColor = color) }

        fun width(width: Float) = apply { config = config.copy(width = width) }

        fun label(label: String) = apply { config = config.copy(label = label) }

        fun drawValues(drawValues: Boolean) = apply { config = config.copy(drawValues = drawValues) }

        fun drawCircles(drawCircles: Boolean) = apply { config = config.copy(drawCircles = drawCircles) }

        fun drawFilled(drawFilled: Boolean) = apply { config = config.copy(drawFilled = drawFilled) }

         */

        fun build(data: List<DailyValues>): LineData = LineData(transformToLineDataSet(data, config))

        private fun transformToLineDataSet(
            data: List<DailyValues>,
            config: LineConfiguration,
        ): LineDataSet {
            val entries = transformToLineEntries(data)
            return LineDataSet(entries, null).apply {
                config.drawValues?.let { setDrawValues(it) }
                config.drawCircles?.let { setDrawCircles(it) }
                config.drawFilled?.let { setDrawFilled(it) }
                config.lineWidth?.let { lineWidth = it }
                config.lineColor?.let { color = it }
                config.mode?.let { mode = it }
                // enableDashedLine(30f, 10f, 0f)
                setDrawHorizontalHighlightIndicator(false)
                setDrawVerticalHighlightIndicator(false)
            }
        }

        private fun transformToLineEntries(data: List<DailyValues>): List<BarEntry> =
            data.mapIndexed { index, dailyValues ->
                BarEntry(index.toFloat(), dailyValues.goal)
            }
    }

    companion object {
        /**
         * Creates a new instance of [BarDataBuilder].
         *
         * @return A new [BarDataBuilder] instance.
         */
        fun barDataBuilder() = BarDataBuilder()

        /**
         * Creates a new instance of [LineDataBuilder].
         *
         * @return A new [LineDataBuilder] instance.
         */
        fun lineDataBuilder() = LineDataBuilder()
    }
}
