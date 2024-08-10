package com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation

import com.github.mikephil.charting.charts.CombinedChart

class YAxisScaler(
    private val yAxisOffsetMultiplier: Float = DEFAULT_Y_OFFSET_MULTIPLIER,
) {
    fun scaleUpMaximum(
        chart: CombinedChart,
        maxDisplayedDataValue: Float,
        onAnimationEnd: (() -> Unit)? = null,
    ) {
        val targetValue = maxDisplayedDataValue * yAxisOffsetMultiplier
        val axisLeft = chart.axisLeft
        val axisRight = chart.axisRight
        val startValue = axisLeft.axisMaximum

        Animator.animate(
            startValue = startValue,
            targetValue = targetValue,
            animationUpdateListener = { newValue ->
                axisLeft.axisMaximum = newValue
                axisRight.axisMaximum = newValue
                chart.notifyDataSetChanged()
                chart.invalidate()
            },
            animationEndListener = {
                onAnimationEnd?.invoke()
            },
        )
    }

    companion object {
        private const val DEFAULT_Y_OFFSET_MULTIPLIER = 1.25f
    }
}
