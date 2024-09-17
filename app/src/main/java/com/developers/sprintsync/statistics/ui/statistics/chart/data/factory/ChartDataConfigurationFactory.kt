package com.developers.sprintsync.statistics.ui.statistics.chart.data.factory

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.global.styleProvider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.statistics.model.chart.configuration.LineConfiguration
import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.entries.CaloriesValueFormatter
import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.entries.DistanceValueFormatter
import com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.entries.DurationValueFormatter
import com.developers.sprintsync.statistics.ui.statistics.chart.data.ChartValuesCalculator
import com.github.mikephil.charting.data.LineDataSet

/**
 * A factory class for creating chart data configurations for bar and line charts.
 *
 * @param context The fragment or activity context.
 */
class ChartDataConfigurationFactory(
    private val context: Context,
) {
    private val colors by lazy { AppThemeProvider(context).Color() }
    private val barStyleProvider by lazy { ResourceTextStyleProvider(context, R.style.ChartLabel_barLabel) }
    private val calculator = ChartValuesCalculator()

    fun createBarConfiguration(
        metric: Metric,
        dailyValues: List<com.developers.sprintsync.statistics.model.chart.chartData.DailyValues>,
    ): BarConfiguration =
        BarConfiguration(
            barColor = colors.secondary,
            barWidth = BAR_WIDTH,
            missingBarColor = colors.secondaryVariant,
            missingBarHeight = calculateMissingBarHeight(dailyValues),
            barLabelColor = barStyleProvider.textColor,
            barLabelSizeDp = barStyleProvider.textSizeDp,
            balLabelTypeFace = barStyleProvider.typeface,
            valueFormatter =
                when (metric) {
                    Metric.DISTANCE -> DistanceValueFormatter()
                    Metric.DURATION -> DurationValueFormatter()
                    Metric.CALORIES -> CaloriesValueFormatter()
                },
        )

    fun createLineConfiguration(): LineConfiguration =
        LineConfiguration(
            lineColor = colors.fourthly,
            lineWidth = LINE_WIDTH,
            drawValues = false,
            drawCircles = false,
            drawFilled = false,
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER,
        )

    private fun calculateMissingBarHeight(
        dailyValues: List<com.developers.sprintsync.statistics.model.chart.chartData.DailyValues>,
    ): Float = calculator.calculateMinGoal(dailyValues) * MISSING_BAR_MULTIPLIER

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.05f
    }
}
