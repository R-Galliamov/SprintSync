package com.developers.sprintsync.presentation.components

import com.developers.sprintsync.R

enum class UIError(@androidx.annotation.StringRes val titleRes: Int) {
    INVALID_WEIGHT_INPUT(R.string.error_invalid_weight_input),
    INVALID_DATE_INPUT(R.string.error_invalid_date_input),
    UNSPECIFIED(R.string.error_unexpected),

    INVALID_TIMESTAMP(R.string.error_run_calculation_failed),
    AVG_PACE_INVALID(R.string.error_run_calculation_failed),
    BEST_PACE_INVALID(R.string.error_run_calculation_failed),
    CALORIES_NEGATIVE(R.string.error_run_calculation_failed),
    DURATION_TOO_SHORT(R.string.error_run_too_short_duration),
    DISTANCE_TOO_SHORT(R.string.error_run_too_short_distance),
    TOO_FEW_SEGMENTS(R.string.error_run_insufficient_data),

    SERVICE_CONNECTION_FAILED(R.string.error_service_connection_failed),
    SERVICE_DISCONNECTED(R.string.error_service_disconnected),
}