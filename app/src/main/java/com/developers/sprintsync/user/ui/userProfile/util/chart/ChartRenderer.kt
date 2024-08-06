package com.developers.sprintsync.user.ui.userProfile.util.chart

import android.animation.ValueAnimator
import android.util.Log
import android.view.MotionEvent
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.ui.userProfile.util.chart.listener.ChartGestureListener
import com.developers.sprintsync.user.ui.userProfile.util.chart.styling.ChartConfigurator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData

class ChartRenderer(
    private val chart: CombinedChart,
) {
    private val configurator: ChartConfigurator by lazy { ChartConfigurator(chart) }

    private var currentStartIndex = 0

    fun renderData(data: WeeklyChartData) {
        val barData = createBarData(data)
        val lineData = createLineData(data)
        val combinedData = createCombinedData(barData, lineData)
        chart.data = combinedData
        configureChart(combinedData, data.referenceTimestamp, data.data.maxOf { it.goal })
        refreshChart()
    }

    private fun refreshChart() {
        chart.data?.let {
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }

    private fun configureChart(
        data: CombinedData,
        referenceTimestamp: Long,
        displayedYAxisValue: Float,
    ) {
        configurator.setAxisLimits(data)
        configurator.configureChart(
            referenceTimestamp,
            displayedYAxisValue,
            object : ChartGestureListener() {
                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float,
                ) {
                    super.onChartFling(me1, me2, velocityX, velocityY)
                    if (velocityX > 0) { // Fling to the left
                        moveBars(-7)
                    }

                    if (velocityX < 0) { // Fling to the left
                        moveBars(7)
                    }
                }

                override fun onChartTranslate(
                    me: MotionEvent?,
                    dX: Float,
                    dY: Float,
                ) {
                    super.onChartTranslate(me, dX, dY)
                    if (dX < 0) { // Check for left swipe
                        // val newXPosition = chart.lowestVisibleX + 7
                        // chart.moveViewToX(newXPosition)
                    }
                }
            },
        )
        configurator.configureVisibleRange()
    }

    private fun moveBars(numBars: Int) {
        val minX = chart.xAxis.axisMinimum
        val maxX = chart.xAxis.axisMaximum

        Log.d("My stack", "minX: $minX")
        Log.d("My stack", "maxX: $maxX")

        Log.d("My stack", "currentStartIndex: $currentStartIndex")
        currentStartIndex = (currentStartIndex + numBars).coerceIn(0, chart.data.xMax.toInt())
        val targetX = (currentStartIndex.toFloat() - 0.5f).coerceIn(minX, maxX)
        animateDragToPosition(targetX, 200)
    }

    fun animateDragToPosition(
        targetX: Float,
        durationMillis: Long = 500,
    ) {
        val startX = chart.lowestVisibleX
        Log.d("My stack", "startX: $startX")
        Log.d("My stack", "targetX: $targetX")
        val distance = targetX - startX

        val startTime = System.currentTimeMillis()
        val endTime = startTime + durationMillis

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = durationMillis

        animator.addUpdateListener { animation ->
            val currentTime = System.currentTimeMillis()
            if (currentTime <= endTime) {
                val progress = (currentTime - startTime) / durationMillis.toFloat()
                val newX = startX + (distance * progress)
                chart.moveViewToX(newX)
                Log.d("My stack", "newX: $newX")
            } else {
                chart.moveViewToX(targetX)
                Log.d("My stack", "Final X: $targetX")
            }
        }
        animator.start()
    }

    private fun createBarData(data: WeeklyChartData): BarData =
        ChartDataTransformer
            .barDataBuilder()
            .configuration(configurator.getBarConfiguration(data))
            .build(data.data)

    private fun createLineData(data: WeeklyChartData): LineData =
        ChartDataTransformer
            .lineDataBuilder()
            .configuration(configurator.getLineConfiguration(data))
            .build(data.data)

    private fun createCombinedData(
        barData: BarData,
        lineData: LineData,
    ): CombinedData =
        CombinedData().apply {
            setData(barData)
            setData(lineData)
        }
}
