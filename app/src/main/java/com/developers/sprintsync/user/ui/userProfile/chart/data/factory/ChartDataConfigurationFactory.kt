package com.developers.sprintsync.user.ui.userProfile.chart.data.factory

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.styleProvider.AppThemeProvider
import com.developers.sprintsync.global.styleProvider.textStyle.ResourceTextStyleProvider
import com.developers.sprintsync.user.model.chart.chartData.DailyValues
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartValuesCalculator
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

    fun createBarConfiguration(dailyValues: List<DailyValues>): BarConfiguration =
        BarConfiguration(
            barColor = colors.secondary,
            barWidth = BAR_WIDTH,
            missingBarColor = colors.secondaryVariant,
            missingBarHeight = calculateMissingBarHeight(dailyValues),
            barLabelColor = barStyleProvider.textColor,
            barLabelSizeDp = barStyleProvider.textSizeDp,
            balLabelTypeFace = barStyleProvider.typeface,
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

    private fun calculateMissingBarHeight(dailyValues: List<DailyValues>): Float =
        calculator.calculateMaxOfGoalAndActualValue(dailyValues) * MISSING_BAR_MULTIPLIER

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.2f
    }
}
