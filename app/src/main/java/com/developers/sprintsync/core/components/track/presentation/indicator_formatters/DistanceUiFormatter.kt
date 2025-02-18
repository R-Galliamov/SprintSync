package com.developers.sprintsync.core.components.track.presentation.indicator_formatters

import com.developers.sprintsync.core.components.track.domain.metrics_converter.MetersToKilometersConverter
import com.developers.sprintsync.core.util.extension.roundedDownNearestTen
import java.util.Locale

object DistanceUiFormatter {
    fun format(
        distanceInMeters: Int,
        uiDistanceUiPattern: DistanceUiPattern,
    ): String {
        val locale = Locale.getDefault()
        val roundedMeters = distanceInMeters.roundedDownNearestTen().toFloat()
        val kilometers = MetersToKilometersConverter.convert(roundedMeters)
        return String.format(locale, uiDistanceUiPattern.pattern, kilometers)
    }
}
