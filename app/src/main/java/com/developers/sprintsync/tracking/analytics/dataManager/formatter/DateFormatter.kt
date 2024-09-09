package com.developers.sprintsync.tracking.analytics.dataManager.formatter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDate(
        timestamp: Long,
        pattern: Pattern,
    ): String {
        val locale = Locale.getDefault()
        val date = Date(timestamp)
        val format = SimpleDateFormat(pattern.format, locale)
        return format.format(date)
    }

    enum class Pattern(
        val format: String,
    ) {
        DAY_MONTH_YEAR("dd.MM.yyyy"),
        DAY_MONTH_YEAR_WEEK_DAY("dd.MM.yyyy, EEE"),
    }
}
