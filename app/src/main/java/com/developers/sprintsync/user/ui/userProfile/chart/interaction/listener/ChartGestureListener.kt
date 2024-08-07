package com.developers.sprintsync.user.ui.userProfile.chart.interaction.listener

import android.view.MotionEvent
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.ui.userProfile.chart.appearance.ChartConfigurator
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataCalculator
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.AnimationHandler
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.ChartNavigator
import com.github.mikephil.charting.charts.CombinedChart
import kotlin.math.roundToInt

/**
 * A custom gesture listener for handling fling events on a chart.
 *
 * @param chart The chart to listen for gestures on.
 * @param navigator The chart navigator for handling week navigation.
 * @param configurator The chart configurator for adjusting chart appearance.
 */
open class ChartGestureListener(
    private val chart: CombinedChart,
    private val navigator: ChartNavigator,
    private val configurator: ChartConfigurator,
) : EmptyChartGestureListener() {
    private val calculator = ChartDataCalculator()
    private val animationHandler by lazy { AnimationHandler(chart) }

    /**
     * Called when a fling gesture is detected on the chart.
     *
     * @param me1 The first down motion event that started the fling.
     * @param me2 The move motion event that triggered the current onFling.
     * @param velocityX The horizontal velocity of the fling.
     * @param velocityY The vertical velocity of the fling.
     */
    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ) {
        val barNumber = chart.visibleXRange.roundToInt()
        if (velocityX > FLING_VELOCITY_THRESHOLD) {
            animationHandler.moveBars(-barNumber)
            navigator.navigateWeek(ChartNavigator.NavigationDirection.PREVIOUS)
            val currentData = navigator.getCurrentWeekData()
            setChartYDisplayedValue(currentData)
            setChartYAxisLimits(currentData)
            configurator.refreshChart()
        }

        if (velocityX < FLING_VELOCITY_THRESHOLD) {
            animationHandler.moveBars(barNumber)
            navigator.navigateWeek(ChartNavigator.NavigationDirection.NEXT)
            val currentData = navigator.getCurrentWeekData()
            setChartYDisplayedValue(currentData)
            setChartYAxisLimits(currentData)
            configurator.refreshChart()
        }
    }

    private fun setChartYDisplayedValue(data: List<DailyDataPoint>) {
        val yMax = calculator.calculateMaxGoal(data)
        configurator.setYaxisDisplayedValue(yMax)
    }

    private fun setChartYAxisLimits(data: List<DailyDataPoint>) {
        val yMax = calculator.calculateMaxOfGoalAndActualValue(data)
        configurator.setYAxisLimits(yMax)
    }

    companion object {
        const val FLING_VELOCITY_THRESHOLD = 0
        const val TAG = "My stack: ChartGestureListener"
    }
}
