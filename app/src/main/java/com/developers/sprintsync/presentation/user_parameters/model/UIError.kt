package com.developers.sprintsync.presentation.user_parameters.model

import com.developers.sprintsync.R

enum class UIError(@androidx.annotation.StringRes val titleRes: Int) {
    INVALID_WEIGHT_INPUT(R.string.error_invalid_weight_input),
    INVALID_DATE_INPUT(R.string.error_invalid_date_input),
    UNSPECIFIED(R.string.error_unexpected),
}