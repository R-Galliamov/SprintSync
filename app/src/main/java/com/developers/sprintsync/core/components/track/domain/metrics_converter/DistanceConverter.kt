package com.developers.sprintsync.core.components.track.domain.metrics_converter

abstract class DistanceConverter {
    abstract fun convert(value: Float): Float

    companion object {
        @JvmStatic
        protected val METERS_IN_KILOMETERS = 1000f
    }
}

object MetersToKilometersConverter : DistanceConverter() {
    override fun convert(value: Float): Float = value / (METERS_IN_KILOMETERS)
}

object KilometersToMetersConverter : DistanceConverter() {
    override fun convert(value: Float): Float = (value * METERS_IN_KILOMETERS)
}
