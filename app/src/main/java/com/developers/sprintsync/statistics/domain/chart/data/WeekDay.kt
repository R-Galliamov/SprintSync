package com.developers.sprintsync.statistics.domain.chart.data

import java.util.Calendar

enum class WeekDay(
    val index: Int,
) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6),
    ;

    fun toCalendarWeekDay(): Int =
        when (this) {
            MONDAY -> Calendar.MONDAY
            TUESDAY -> Calendar.TUESDAY
            WEDNESDAY -> Calendar.WEDNESDAY
            THURSDAY -> Calendar.THURSDAY
            FRIDAY -> Calendar.FRIDAY
            SATURDAY -> Calendar.SATURDAY
            SUNDAY -> Calendar.SUNDAY
        }
}
