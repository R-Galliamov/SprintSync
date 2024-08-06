package com.developers.sprintsync.user.ui.userProfile.util.chart.newChart

import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.listener.OnChartGestureListener

class ChartInteractionHandler(
    private val chart: CombinedChart,
) {
    fun configureInteraction(onGestureListener: OnChartGestureListener? = null) {
        chart.apply {
            setTouchEnabled(true)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            onGestureListener?.let { onChartGestureListener = it }
        }
    }
}
