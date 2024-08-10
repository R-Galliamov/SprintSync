package com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation

import com.github.mikephil.charting.charts.CombinedChart

class BarAnimator(
    private val chart: CombinedChart,
) {
    fun moveBars(
        fromBarIndex: Int,
        numBars: Int,
    ) {
        val startBarIndex = fromBarIndex.toFloat()
        val targetXPosition = calculateTargetXPosition(fromBarIndex + numBars, calculateXOffset())
        Animator.animate(startBarIndex, targetXPosition, animationUpdateListener = {
            chart.moveViewToX(it)
        })
    }

    private fun calculateTargetXPosition(
        targetBarIndex: Int,
        xOffset: Float,
    ): Float = (targetBarIndex.toFloat() - xOffset).coerceIn(chart.xAxis.axisMinimum, chart.xAxis.axisMaximum)

    private fun calculateXOffset(): Float {
        val firstBarEntry = chart.data.getDataSetByIndex(FIRST_DATA_INDEX).getEntryForIndex(FIRST_DATA_INDEX)
        return firstBarEntry?.x?.minus(chart.xAxis.axisMinimum) ?: EMPTY_DATA_DEFAULT_VALUE
    }

    companion object {
        private const val EMPTY_DATA_DEFAULT_VALUE = 0f

        private const val FIRST_DATA_INDEX = 0
    }
}
