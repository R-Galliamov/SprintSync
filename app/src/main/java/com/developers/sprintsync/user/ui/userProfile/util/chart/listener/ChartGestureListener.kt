package com.developers.sprintsync.user.ui.userProfile.util.chart.listener

import android.view.MotionEvent
import com.developers.sprintsync.user.ui.userProfile.util.chart.newChart.AnimationHandler
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import kotlin.math.roundToInt

open class ChartGestureListener(
    private val chart: CombinedChart,
) : OnChartGestureListener {
    private val animationHandler by lazy { AnimationHandler(chart) }

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ) {
        val barNumber = chart.visibleXRange.roundToInt()
        if (velocityX > FLING_VELOCITY_THRESHOLD) {
            animationHandler.moveBars(-barNumber)
        }

        if (velocityX < FLING_VELOCITY_THRESHOLD) {
            animationHandler.moveBars(barNumber)
        }
    }

    override fun onChartTranslate(
        me: MotionEvent?,
        dX: Float,
        dY: Float,
    ) {
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

    override fun onChartScale(
        me: MotionEvent?,
        scaleX: Float,
        scaleY: Float,
    ) {
    }

    companion object {
        const val FLING_VELOCITY_THRESHOLD = 0
    }
}
