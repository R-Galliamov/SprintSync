package com.developers.sprintsync.statistics.ui.statistics.chart.interaction.listener

import android.view.MotionEvent
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

/**
 * An empty implementation of the [OnChartGestureListener] interface, providing default empty implementations for all methods.
 * This class can be extended to create custom gesture listeners that only need to override specific methods.
 */
open class EmptyChartGestureListener : OnChartGestureListener {
    override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?,
    ) {
    }

    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?,
    ) {
    }

    override fun onChartLongPressed(me: MotionEvent?) {
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {
    }

    override fun onChartSingleTapped(me: MotionEvent?) {
    }

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ) {
    }

    override fun onChartScale(
        me: MotionEvent?,
        scaleX: Float,
        scaleY: Float,
    ) {
    }

    override fun onChartTranslate(
        me: MotionEvent?,
        dX: Float,
        dY: Float,
    ) {
    }
}
