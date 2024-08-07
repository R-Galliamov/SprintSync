package com.developers.sprintsync.user.ui.userProfile.chart.data

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.global.styleProvider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
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

    /**
     * Creates a [BarConfiguration] instance based on the provided [WeeklyChartData].
     *
     * @param data The weekly chart data to configure the bar chart.
     * @return A configured [BarConfiguration] object.
     */
    fun createBarConfiguration(data: WeeklyChartData): BarConfiguration =
        BarConfiguration(
            barColor = colors.secondary,
            barWidth = BAR_WIDTH,
            missingBarColor = colors.secondaryVariant,
            missingBarHeight = calculateMissingBarHeight(data.data),
            barLabelColor = barStyleProvider.textColor,
            barLabelSizeDp = barStyleProvider.textSizeDp,
            balLabelTypeFace = barStyleProvider.typeface,
            dataLabel = data.label,
        )

    /**
     * Creates a [LineConfiguration] instance based on the provided [WeeklyChartData].
     *
     * @param data The weekly chart data to configure the line chart.
     * @return A configured[LineConfiguration] object.
     */
    fun createLineConfiguration(data: WeeklyChartData): LineConfiguration =
        LineConfiguration(
            lineColor = colors.fourthly,
            lineWidth = LINE_WIDTH,
            label = data.label,
            drawValues = false,
            drawCircles = false,
            drawFilled = false,
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER,
        )

    /**
     * Calculates the height of bars representing missing data points.
     *
     * @param data The list of daily data points.
     * @return The calculated height for missing bars.
     */
    private fun calculateMissingBarHeight(data: List<DailyDataPoint>): Float = data.minOf { it.goal } * MISSING_BAR_MULTIPLIER

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.2f
    }
}
