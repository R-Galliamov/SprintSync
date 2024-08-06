package com.developers.sprintsync.user.ui.userProfile.util.chart.listener

import android.util.Log
import android.view.MotionEvent
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

open class ChartGestureListener : OnChartGestureListener {
    override fun onChartTranslate(
        me: MotionEvent?,
        dX: Float,
        dY: Float,
    ) {
        Log.d("My stack", "onChartTranslate: $dX")
    }

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
        Log.d("My stack", "onChartFling: $velocityX")
    }

    override fun onChartScale(
        me: MotionEvent?,
        scaleX: Float,
        scaleY: Float,
    ) {
    }
}
