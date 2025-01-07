package com.developers.sprintsync.home.statistics.presentation.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import com.developers.sprintsync.R
import com.developers.sprintsync.core.components.track.presentation.model.FormattedDurationParts

class SpannableStyler(
    private val context: Context,
) {
    fun styleDuration(formattedDuration: FormattedDurationParts): CharSequence {
        val hhMmSpan = SpannableString(formattedDuration.hhMm)
        hhMmSpan.setSpan(
            TextAppearanceSpan(context, R.style.TabloidText),
            0,
            hhMmSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )

        val ssSpan = SpannableString(formattedDuration.ss)
        ssSpan.setSpan(
            TextAppearanceSpan(context, R.style.RegularText_Gray),
            0,
            ssSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
        )
        return SpannableStringBuilder().append(hhMmSpan).append(ssSpan)
    }
}
