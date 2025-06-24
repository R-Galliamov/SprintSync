package com.developers.sprintsync.presentation.workouts_stats.chart.factory

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.style_provider.AppThemeProvider
import com.developers.sprintsync.core.util.style_provider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.presentation.workouts_stats.chart.data.DailyValues
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.presentation.workouts_stats.chart.config.LineConfiguration
import com.developers.sprintsync.presentation.workouts_stats.chart.config.BarConfiguration
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries.CaloriesValueFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries.DistanceValueFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.formatters.entries.DurationValueFormatter
import com.developers.sprintsync.presentation.workouts_stats.chart.data.calculator.ValuesCalculator
import com.github.mikephil.charting.data.LineDataSet

/**
 * A factory class for creating chart data configurations for bar and line charts.
 *
 * @param context The fragment or activity context.
 */
class DataConfigFactory(
    private val context: Context,
) {
    private val colors by lazy { AppThemeProvider(context).Color() }
    private val barStyleProvider by lazy { ResourceTextStyleProvider(context, R.style.ChartLabel_barLabel) }
    private val calculator = ValuesCalculator()

    fun createBarConfiguration(
        metric: Metric,
        dailyValues: List<DailyValues>,
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
        dailyValues: List<DailyValues>,
    ): Float = calculator.calculateMinGoal(dailyValues) * MISSING_BAR_MULTIPLIER

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.05f
    }
}
