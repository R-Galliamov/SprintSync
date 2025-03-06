package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import com.developers.sprintsync.core.util.time.TimeParts
import java.util.Locale
import java.util.concurrent.TimeUnit

object DurationUiFormatter {
    fun format(
        milliseconds: Long,
        durationUiPattern: DurationUiPattern,
    ): String = DurationStrategyFactory.getFactory(durationUiPattern).format(milliseconds)
}

enum class DurationUiPattern {
    HH_MM_SS,
    MM,
    MM_UNIT,
}

object DurationStrategyFactory {
    fun getFactory(durationUiPattern: DurationUiPattern): DurationFormatStrategy =
        when (durationUiPattern) {
            DurationUiPattern.HH_MM_SS -> DurationFormatStrategy.HhMmSs
            DurationUiPattern.MM -> DurationFormatStrategy.Mm
            DurationUiPattern.MM_UNIT -> DurationFormatStrategy.MmUnit
        }
}

sealed interface DurationFormatStrategy {
    fun format(milliseconds: Long): String

    data object HhMmSs : DurationFormatStrategy {
        private const val PATTERN = "%02d:%02d:%02d"

        override fun format(milliseconds: Long): String {
            if (milliseconds < 0) throw IllegalArgumentException("Duration cannot be negative")
            val locale: Locale = Locale.getDefault()
            val timeParts = TimeParts.create(milliseconds)
            return PATTERN.format(locale, timeParts.hours, timeParts.minutes, timeParts.seconds)
        }
    }

    data object Mm : DurationFormatStrategy {
        override fun format(milliseconds: Long): String {
            if (milliseconds < 0) throw IllegalArgumentException("Duration cannot be negative")
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            return minutes.toString()
        }
    }

    data object MmUnit : DurationFormatStrategy {
        override fun format(milliseconds: Long): String {
            if (milliseconds < 0) throw IllegalArgumentException("Duration cannot be negative")
            val locale: Locale = Locale.getDefault()
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            return PATTERN.format(locale, minutes)
        }

        private const val PATTERN = "%1\$d min"
    }
}
