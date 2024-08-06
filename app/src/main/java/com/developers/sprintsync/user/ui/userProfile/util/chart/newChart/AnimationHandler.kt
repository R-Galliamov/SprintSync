package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import android.animation.ValueAnimator
import com.github.mikephil.charting.charts.CombinedChart

class AnimationHandler(
    private val chart: CombinedChart,
) {
    private var currentStartIndex: Int = START_INDEX

    fun moveBars(numBars: Int) {
        val minX = chart.xAxis.axisMinimum
        val maxX = chart.xAxis.axisMaximum
        val xOffset = chart.xAxis.xOffset

        currentStartIndex = (currentStartIndex + numBars).coerceIn(FIRST_VISIBLE_BAR_INDEX, chart.data.xMax.toInt())
        val targetX = (currentStartIndex.toFloat() - xOffset).coerceIn(minX, maxX)
        animateDragToPosition(targetX)
    }

    private fun animateDragToPosition(
        targetX: Float,
        durationMillis: Long = ANIMATION_DURATION_MILLIS,
    ) {
        val startX = chart.lowestVisibleX
        val distance = targetX - startX

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
            } else {
                chart.moveViewToX(targetX)
            }
        }
        animator.start()
    }

    companion object {
        private const val ANIMATION_DURATION_MILLIS = 200L
        private const val ANIMATION_START_VALUE = 0f
        private const val ANIMATION_END_VALUE = 1f

        private const val FIRST_VISIBLE_BAR_INDEX = 0
        private const val START_INDEX = 0
    }
}
