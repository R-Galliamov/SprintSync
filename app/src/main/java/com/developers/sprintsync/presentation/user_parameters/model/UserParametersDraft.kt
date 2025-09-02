package com.developers.sprintsync.presentation.user_parameters.model

import com.developers.sprintsync.domain.user_profile.model.Sex

data class UserParametersDraft(
    val sex: Sex? = null,
    val birthDateEpochMillis: Long? = null,
    val birthDateText: String = "",
    val weightText: String = "",
)