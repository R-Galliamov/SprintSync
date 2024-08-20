package com.developers.sprintsync.user.ui.userProfile.chart.data.transformer

import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.chartData.IndexedDailyValues
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * A class responsible for transforming chart data into formats suitable for display using MPAndroidChart library.
 */
class ChartDataTransformer {
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

        fun build(data: IndexedDailyValues): BarData {
            val presentData =
                data.filterValues { it is DailyValues.Present }.mapValues { it.value as DailyValues.Present }
            val missingData =
                data.filterValues { it is DailyValues.Missing }.mapValues { it.value as DailyValues.Missing }

            val presentDataSet = transformToPresentDataSet(presentData, config)
            val missingDataSet = transformToMissingDataSet(missingData, config)
            return BarData(presentDataSet, missingDataSet).apply {
                config.barWidth?.let { barWidth = it }
            }
        }

        private fun transformToPresentDataSet(
            data: Map<Int, DailyValues.Present>,
            config: BarConfiguration,
        ): BarDataSet {
            val entries = transformToPresentEntries(data)
            return BarDataSet(entries, null).apply {
                config.barColor?.let { color = it }
                config.barLabelColor?.let { valueTextColor = it }
                config.balLabelTypeFace?.let { valueTypeface = it }
                config.barLabelSizeDp?.let { valueTextSize = it }
                config.valueFormatter?.let { valueFormatter = it }
                isHighlightEnabled = false
            }
        }

        private fun transformToMissingDataSet(
            data: Map<Int, DailyValues.Missing>,
            config: BarConfiguration,
        ): BarDataSet {
            val entries = transformToMissingEntries(data, config.missingBarHeight)
            return BarDataSet(entries, null).apply {
                config.missingBarColor?.let { color = it }
                setDrawValues(false)
                isHighlightEnabled = false
            }
        }

        private fun transformToPresentEntries(data: Map<Int, DailyValues.Present>): List<BarEntry> =
            data.map { BarEntry(it.key.toFloat(), it.value.actualValue) }

        private fun transformToMissingEntries(
            data: Map<Int, DailyValues.Missing>,
            missingBarHeight: Float,
        ): List<BarEntry> = data.map { BarEntry(it.key.toFloat(), missingBarHeight) }
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

        fun build(data: IndexedDailyValues): LineData = LineData(transformToLineDataSet(data, config))

        private fun transformToLineDataSet(
            data: IndexedDailyValues,
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

        private fun transformToLineEntries(data: IndexedDailyValues): List<BarEntry> =
            data.map { BarEntry(it.key.toFloat(), it.value.goal) }
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
