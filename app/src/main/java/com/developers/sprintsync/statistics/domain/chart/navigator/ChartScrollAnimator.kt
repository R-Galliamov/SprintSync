package com.developers.sprintsync.statistics.domain.chart.navigator

import com.developers.sprintsync.core.util.animator.Animator
import com.github.mikephil.charting.charts.CombinedChart

class ChartScrollAnimator(
    private val chart: CombinedChart,
) {
    fun animateBarScroll(
        fromBarIndex: Int,
        numBars: Int,
    ) {
        val startBarIndex = fromBarIndex.toFloat()
        val targetXPosition = getTargetXPosition(fromBarIndex + numBars, getXOffset())
        Animator.startAnimation(startBarIndex, targetXPosition, animationUpdateListener = {
            chart.moveViewToX(it)
        })
    }

    private fun getTargetXPosition(
        targetBarIndex: Int,
        xOffset: Float,
    ): Float = (targetBarIndex.toFloat() - xOffset).coerceIn(chart.xAxis.axisMinimum, chart.xAxis.axisMaximum)

    private fun getXOffset(): Float {
        val firstBarEntry = chart.data.getDataSetByIndex(FIRST_DATA_INDEX).getEntryForIndex(FIRST_DATA_INDEX)
        return firstBarEntry?.x?.minus(chart.xAxis.axisMinimum) ?: EMPTY_DATA_DEFAULT_VALUE
    }

    companion object {
        private const val EMPTY_DATA_DEFAULT_VALUE = 0f

        private const val FIRST_DATA_INDEX = 0
    }
}
