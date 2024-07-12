package com.developers.sprintsync.tracking.analytics.dataManager.formatter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatter {
    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
        private val defaultLocale by lazy { Locale.getDefault() }

        fun formatDate(
            timestamp: Long,
            locale: Locale = defaultLocale,
        ): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat(DATE_FORMAT, locale)
            return format.format(date)
        }
    }
}
