package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import android.animation.ValueAnimator
import android.util.Log
import com.github.mikephil.charting.charts.CombinedChart

class AnimationHandler(
    private val chart: CombinedChart,
) {
    private var currentStartIndex: Int = START_INDEX

    fun moveBars(numBars: Int) {
        val minX = chart.xAxis.axisMinimum
        val maxX = chart.xAxis.axisMaximum
        val xOffset = calculateXOffset()

        Log.d(TAG, "minX: $minX, maxX: $maxX, xOffset: $xOffset")

        currentStartIndex = (currentStartIndex + numBars).coerceIn(FIRST_VISIBLE_BAR_INDEX, chart.data.xMax.toInt())
        Log.d(TAG, "currentStartIndex: $currentStartIndex")
        val targetX = (currentStartIndex.toFloat() - xOffset).coerceIn(minX, maxX)
        animateDragToPosition(targetX)
    }

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
                Log.d(TAG, "newX: $newX")
            } else {
                chart.moveViewToX(targetX)
                Log.d(TAG, "Final X: $targetX")
            }
        }
        animator.start()
    }

    private fun calculateXOffset(): Float {
        val firstBarEntry = chart.data.getDataSetByIndex(START_DATA_INDEX).getEntryForIndex(START_DATA_INDEX)
        val minX = chart.xAxis.axisMinimum
        return firstBarEntry.x - minX
    }

    companion object {
        private const val ANIMATION_DURATION_MILLIS = 200L
        private const val ANIMATION_START_VALUE = 0f
        private const val ANIMATION_END_VALUE = 1f

        private const val FIRST_VISIBLE_BAR_INDEX = 0
        private const val START_INDEX = 0

        private const val START_DATA_INDEX = 0

        private const val TAG = "My Stack: AnimationHandler"
    }
}
