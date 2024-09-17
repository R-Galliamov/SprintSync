package com.developers.sprintsync.statistics.model.ui

import com.developers.sprintsync.statistics.model.chart.navigator.RangePosition

data class FormattedDateRange(
    val dayMonthRange: String,
    val yearsRange: String,
    val position: RangePosition,
) {
    companion object {
        private const val EMPTY_STRING = ""
        val EMPTY = FormattedDateRange(EMPTY_STRING, EMPTY_STRING, RangePosition.NOT_INITIALIZED)
    }
}
