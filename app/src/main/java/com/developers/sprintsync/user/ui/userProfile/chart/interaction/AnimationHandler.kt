package com.developers.sprintsync.user.ui.userProfile.chart.interaction

import android.animation.ValueAnimator
import android.util.Log
import com.github.mikephil.charting.charts.CombinedChart
import kotlin.math.roundToInt

/**
 * A class for handling animations related to chart interactions, such as moving bars.
 *
 * @param chart The chart to apply animations to.
 */
class AnimationHandler(
    private val chart: CombinedChart,
) {
    private var currentStartIndex = START_INDEX

    /**
     * Moves the displayed bars of the chart by a specified number.
     *
     * @param numBars The number of bars to move. Positive values move to the right, negative to the left.
     */
    fun moveBars(numBars: Int) {
        updateCurrentStartIndex(numBars)
        val targetX = calculateTargetX(calculateXOffset())
        animateDragToPosition(targetX)
    }

    /** Animates the chart to smoothly move to a target X position.
     *
     * @param targetX The target X position to move the chart to.
     * @param durationMillis The duration of the animation in milliseconds.
     */
    private fun animateDragToPosition(
        targetX: Float,
        durationMillis: Long = ANIMATION_DURATION_MILLIS,
    ) {
        val startX = chart.lowestVisibleX
        val distance = targetX - startX

        Log.d(TAG, "startX: $startX, targetX: $targetX, distance: $distance")

        val startTime = System.currentTimeMillis()
        val endTime = startTime + durationMillis

        val animator = ValueAnimator.ofFloat(ANIMATION_START_VALUE, ANIMATION_END_VALUE)
        animator.duration = durationMillis

        animator.addUpdateListener { animation ->
            val currentTime = System.currentTimeMillis()
            if (currentTime <= endTime) {
                val progress = (currentTime - startTime) / durationMillis.toFloat()
                val newX = startX + (distance * progress)
                chart.moveViewToX(newX)
                // Log.d(TAG, "newX: $newX")
            } else {
                chart.moveViewToX(targetX)
                //  Log.d(TAG, "Final X: $targetX")
            }
        }
        animator.start()
    }

    private fun updateCurrentStartIndex(numBars: Int) {
        currentStartIndex = calculateCurrentStartIndex(numBars)
    }

    private fun calculateCurrentStartIndex(numBars: Int): Int {
        val maxBarIndex = chart.data.xMax.toInt() - calculateMaxIndexOffset()
        return (currentStartIndex + numBars).coerceIn(
            FIRST_VISIBLE_BAR_INDEX,
            maxBarIndex,
        )
    }

    private fun calculateTargetX(xOffset: Float) =
        (currentStartIndex.toFloat() - xOffset).coerceIn(chart.xAxis.axisMinimum, chart.xAxis.axisMaximum)

    private fun calculateMaxIndexOffset(): Int {
        val visibleXRange = (chart.highestVisibleX - chart.lowestVisibleX).roundToInt()
        Log.d(TAG, "visibleXRange: $visibleXRange")
        return (visibleXRange - RIGHT_EDGE_OFFSET)
    }

    private fun calculateXOffset(): Float {
        val firstBarEntry = chart.data.getDataSetByIndex(START_DATA_INDEX).getEntryForIndex(START_DATA_INDEX)
        return firstBarEntry?.x?.minus(chart.xAxis.axisMinimum) ?: EMPTY_DATA_DEFAULT_VALUE
    }

    companion object {
        private const val EMPTY_DATA_DEFAULT_VALUE = 0f

        private const val ANIMATION_DURATION_MILLIS = 200L
        private const val ANIMATION_START_VALUE = 0f
        private const val ANIMATION_END_VALUE = 1f

        private const val FIRST_VISIBLE_BAR_INDEX = 0
        private const val START_INDEX = 0

        private const val START_DATA_INDEX = 0

        private const val RIGHT_EDGE_OFFSET = 1

        private const val TAG = "My Stack: AnimationHandler"
    }
}
