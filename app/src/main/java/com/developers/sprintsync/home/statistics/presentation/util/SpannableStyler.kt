package com.developers.sprintsync.home.statistics.presentation.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.time.TimeParts
import java.util.Locale

class SpannableStyler(
    private val context: Context,
) {
    fun styleDuration(
        timeParts: TimeParts,
        locale: Locale = Locale.getDefault(),
    ): CharSequence {
        val hhMm = String.format(locale, PATTERN_HH_MM, timeParts.hours, timeParts.minutes)

        val hhMmSpan = SpannableString(hhMm)
        hhMmSpan.setSpan(
            TextAppearanceSpan(context, R.style.TabloidText),
            0,
            hhMmSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )

        val ss = String.format(locale, PATTERN_SS, timeParts.seconds)

        val ssSpan = SpannableString(ss)
        ssSpan.setSpan(
            TextAppearanceSpan(context, R.style.RegularText_Gray),
            0,
            ssSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        return SpannableStringBuilder().append(hhMmSpan).append(ssSpan)
    }

    companion object {
        private const val PATTERN_HH_MM = "%02d:%02d"
        private const val PATTERN_SS = ":%02d"
    }
}
