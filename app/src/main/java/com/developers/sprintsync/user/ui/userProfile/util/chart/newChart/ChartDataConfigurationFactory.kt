package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.manager.AppThemeManager
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.model.chart.configuration.BarConfiguration
import com.developers.sprintsync.user.model.chart.configuration.LineConfiguration
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.styleProvider.ResourceTextStyleProvider
import com.github.mikephil.charting.data.LineDataSet

class ChartDataConfigurationFactory(
    private val context: Context,
) {
    private val colors by lazy { AppThemeManager(context).Color() }
    private val barStyleProvider by lazy { ResourceTextStyleProvider(context, R.style.ChartLabel_barLabel) }

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

    private fun calculateMissingBarHeight(data: List<DailyDataPoint>): Float = data.minOf { it.goal } * MISSING_BAR_MULTIPLIER

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val LINE_WIDTH = 2f
        private const val MISSING_BAR_MULTIPLIER = 0.2f
    }
}
