package com.developers.sprintsync.user.model

data class FormattedDateRange(
    val dayMonthRange: String,
    val yearsRange: String,
) {
    companion object {
        val EMPTY = FormattedDateRange("", "")
    }
}
