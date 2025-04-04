package com.developers.sprintsync.presentation.workouts_statistics.chart.interaction

import android.util.Log
import android.view.MotionEvent
import com.developers.sprintsync.presentation.workouts_statistics.chart.navigator.ChartNavigator

/**
 * A custom gesture listener for handling fling events on a chart.
 *
 * @param navigator The chart navigator for handling week navigation.
 */
open class ChartGestureListener(
    private val navigator: ChartNavigator,
) : EmptyChartGestureListener() {
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
        if (velocityX > FLING_VELOCITY_THRESHOLD) {
            navigator.shiftViewPortRange(ChartNavigator.NavigationDirection.PREVIOUS)
            Log.d(TAG, "onChartFling: previous")
        }

        if (velocityX < FLING_VELOCITY_THRESHOLD) {
            navigator.shiftViewPortRange(ChartNavigator.NavigationDirection.NEXT)
            Log.d(TAG, "onChartFling: next")
        }
    }

    companion object {
        const val FLING_VELOCITY_THRESHOLD = 0
        const val TAG = "My stack: ChartGestureListener"
    }
}
