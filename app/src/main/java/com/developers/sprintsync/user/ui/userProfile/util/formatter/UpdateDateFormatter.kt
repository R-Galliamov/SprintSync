package com.developers.sprintsync.user.ui.userProfile.util.formatter

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.global.util.formatter.DateFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UpdateDateFormatter
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val title =
            context.getString(
                R.string.last_update,
            )

        fun formatTimestamp(timestamp: Long?): String {
            if (timestamp == null) return EMPTY_DATE
            val date = DateFormatter.formatDate(timestamp, DateFormatter.Pattern.DAY_MONTH_YEAR)
            return "$title $date"
        }

        companion object {
            private const val EMPTY_DATE = ""
        }
    }
