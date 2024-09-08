package com.developers.sprintsync.user.model.ui

import com.developers.sprintsync.user.model.chart.navigator.RangePosition

data class FormattedDateRange(
    val dayMonthRange: String,
    val yearsRange: String,
    val position: RangePosition,
) {
    companion object {
        val EMPTY = FormattedDateRange("", "", RangePosition.NOT_INITIALIZED)
    }
}
