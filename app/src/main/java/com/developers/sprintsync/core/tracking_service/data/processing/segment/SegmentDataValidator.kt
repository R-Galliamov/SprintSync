package com.developers.sprintsync.core.tracking_service.data.processing.segment

sealed class SegmentDataValidator(
    protected val durationMillis: Long,
) {
    abstract val dataIsValid: Boolean

    class ActiveSegmentDataValidator(
        private val distanceMeters: Float,
        durationMillis: Long,
    ) : SegmentDataValidator(durationMillis) {
        override val dataIsValid: Boolean
            get() = (distanceMeters > VALIDATION_THRESHOLD_METERS && durationMillis > VALIDATION_THRESHOLD_MILLIS)
    }

    class InactiveSegmentDataValidator(
        durationMillis: Long,
    ) : SegmentDataValidator(durationMillis) {
        override val dataIsValid: Boolean
            get() = durationMillis > VALIDATION_THRESHOLD_MILLIS
    }

    protected companion object {
        const val VALIDATION_THRESHOLD_METERS = 0f
        const val VALIDATION_THRESHOLD_MILLIS = 0L
    }
}