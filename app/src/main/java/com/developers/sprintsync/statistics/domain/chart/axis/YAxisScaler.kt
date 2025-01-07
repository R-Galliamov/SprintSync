package com.developers.sprintsync.statistics.domain.chart.axis

import com.developers.sprintsync.core.util.animator.Animator
import com.github.mikephil.charting.charts.CombinedChart

class YAxisScaler(
    private val yAxisOffsetMultiplier: Float = DEFAULT_Y_OFFSET_MULTIPLIER,
) {
    fun scaleUpMaximum(
        chart: CombinedChart,
        maxDisplayedDataValue: Float,
    ) {
        val newMaxValue = maxDisplayedDataValue * yAxisOffsetMultiplier
        updateYAxis(chart, newMaxValue)
    }

    fun scaleUpMaximumAnimated(
        chart: CombinedChart,
        maxDisplayedDataValue: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) {
        val targetValue = maxDisplayedDataValue * yAxisOffsetMultiplier
        val startValue = chart.axisLeft.axisMaximum

        Animator.startAnimation(
            startValue = startValue,
            targetValue = targetValue,
            animationUpdateListener = { newValue ->
                updateYAxis(chart, newValue)
            },
            animationEndListener = {
                onAnimationEnd?.invoke()
            },
        )
    }

    private fun updateYAxis(
        chart: CombinedChart,
        newMaxValue: Float,
    ) {
        val leftAxis = chart.axisLeft
        val rightAxis = chart.axisRight

        leftAxis.axisMinimum = 0f
        rightAxis.axisMinimum = 0f
        leftAxis.axisMaximum = newMaxValue
        rightAxis.axisMaximum = newMaxValue

        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    companion object {
        private const val DEFAULT_Y_OFFSET_MULTIPLIER = 1.25f
    }
}
