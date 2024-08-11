package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.ChartData

class ChartDataLoader {
    companion object {
        val referenceTimestamp = System.currentTimeMillis()
    }
    class Duration {
        private val point1 = DailyDataPoint.Present(0, 10f, 10f)
        private val point2 = DailyDataPoint.Present(1, 10f, 10f)
        private val point3 = DailyDataPoint.Present(2, 10f, 10f)
        private val point4 = DailyDataPoint.Present(3, 10f, 10f)
        private val point5 = DailyDataPoint.Missing(4, 10f)
        private val point6 = DailyDataPoint.Missing(5, 10f)
        private val point7 = DailyDataPoint.Missing(6, 10f)

        private val point8 = DailyDataPoint.Present(7, 20f, 10f)
        private val point9 = DailyDataPoint.Present(8, 20f, 10f)
        private val point10 = DailyDataPoint.Present(9, 20f, 10f)
        private val point11 = DailyDataPoint.Present(10, 20f, 10f)
        private val point12 = DailyDataPoint.Missing(11, 20f)
        private val point13 = DailyDataPoint.Missing(12, 20f)
        private val point14 = DailyDataPoint.Missing(13, 20f)

        private val point15 = DailyDataPoint.Present(14, 30f, 10f)
        private val point16 = DailyDataPoint.Present(15, 30f, 10f)
        private val point17 = DailyDataPoint.Present(16, 30f, 10f)
        private val point18 = DailyDataPoint.Present(17, 30f, 10f)
        private val point19 = DailyDataPoint.Missing(18, 30f)
        private val point20 = DailyDataPoint.Missing(19, 30f)
        private val point21 = DailyDataPoint.Missing(20, 30f)

        private val list1 =
            listOf(
                point1,
                point2,
                 point3,
                 point4,
                 point5,
                 point6,
                 point7,
                 point8,
            )

        private val list2 = listOf(point8, point9, point10, point11, point12, point13, point14)

        private val list3 = listOf(point15, point16, point17, point18, point19, point20, point21)

        private val listGeneral = list1.plus(list2).plus(list3)

        val testData1 = ChartData("Test", referenceTimestamp, list1)
        val testData2 = ChartData("Test", referenceTimestamp, list2)
        val testData3 = ChartData("Test", referenceTimestamp, list3)

        val testDataGeneral = ChartData("Test", referenceTimestamp, listGeneral)
    }

    class Distance {
        private val point1 = DailyDataPoint.Present(0, 1f, 1f)
        private val point2 = DailyDataPoint.Present(1, 1f, 1f)
        private val point3 = DailyDataPoint.Present(2, 1f, 1f)
        private val point4 = DailyDataPoint.Present(3, 1f, 1f)
        private val point5 = DailyDataPoint.Missing(4, 1f)
        private val point6 = DailyDataPoint.Missing(5, 1f)
        private val point7 = DailyDataPoint.Missing(6, 2f)

        private val point8 = DailyDataPoint.Present(7, 2.7f, 1f)
        private val point9 = DailyDataPoint.Present(8, 2f, 1f)
        private val point10 = DailyDataPoint.Present(9, 2f, 1f)
        private val point11 = DailyDataPoint.Present(10, 2f, 1f)
        private val point12 = DailyDataPoint.Missing(11, 3f)
        private val point13 = DailyDataPoint.Missing(12, 3f)
        private val point14 = DailyDataPoint.Missing(13, 5f)

        private val point15 = DailyDataPoint.Present(14, 3.1f, 1f)
        private val point16 = DailyDataPoint.Present(15, 3f, 1f)
        private val point17 = DailyDataPoint.Present(16, 3f, 1f)
        private val point18 = DailyDataPoint.Present(17, 3f, 1f)
        private val point19 = DailyDataPoint.Missing(18, 3f)
        private val point20 = DailyDataPoint.Missing(19, 3f)
        private val point21 = DailyDataPoint.Present(20, 1000f, 1000f)

        private val list1 =
            listOf(
                point1,
                point2,
                point3,
                point4,
                point5,
                point6,
                point7,
            )

        private val list2 = listOf(point8, point9, point10, point11, point12, point13, point14)

        private val list3 = listOf(point15, point16, point17, point18, point19, point20, point21)

        private val listGeneral = list1.plus(list2).plus(list3)

        val testData1 = ChartData("Test", referenceTimestamp, list1)
        val testData2 = ChartData("Test", referenceTimestamp, list2)
        val testData3 = ChartData("Test", referenceTimestamp, list3)

        val testDataGeneral = ChartData("Test", referenceTimestamp, listGeneral)
    }
}
