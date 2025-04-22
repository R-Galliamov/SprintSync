package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event

import android.content.Context
import com.developers.sprintsync.R
import com.developers.sprintsync.data.track.service.TrackingServiceException
import com.developers.sprintsync.domain.track.use_case.validator.TrackValidationException
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * Provides messages for UI based on tracking exceptions.
 */
@ViewModelScoped
class TrackingErrorMessageProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Converts an exception to a message for UI display.
         *
         * @param e The exception to convert.
         * @return A string message suitable for showing to the user.
         */
        fun toUiMessage(e: Exception): String =
            when (e) {
                is TrackValidationException -> handleValidationException(e)
                is TrackingServiceException -> handleTrackingServiceException(e)
                else -> context.getString(R.string.err_unexpected)
            }

        private fun handleValidationException(e: TrackValidationException): String =
            when (e) {
                is TrackValidationException.InvalidTimestamp,
                is TrackValidationException.AvgPaceInvalid,
                is TrackValidationException.BestPaceInvalid,
                is TrackValidationException.CaloriesNegative,
                ->
                    context.getString(R.string.err_track_calculation)

                is TrackValidationException.DurationTooShort ->
                    context.getString(R.string.err_track_short_duration)

                is TrackValidationException.DistanceTooShort ->
                    context.getString(R.string.err_track_short_distance)

                is TrackValidationException.TooFewSegments ->
                    context.getString(R.string.err_track_few_segments)
            }

        private fun handleTrackingServiceException(e: TrackingServiceException): String =
            when (e) {
                is TrackingServiceException.BindingFailed -> context.getString(R.string.err_svc_binding)
                is TrackingServiceException.ServiceDisconnected -> context.getString(R.string.err_svc_disconnected)
            }
    }
