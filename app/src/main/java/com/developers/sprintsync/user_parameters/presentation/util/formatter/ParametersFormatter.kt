package com.developers.sprintsync.user_parameters.presentation.util.formatter

import com.developers.sprintsync.core.util.formatter.DateFormatter
import com.developers.sprintsync.user_parameters.model.UserParameters
import com.developers.sprintsync.user_parameters.presentation.fragment.UserParametersFormatted

object ParametersFormatter {
    fun format(parameters: UserParameters): UserParametersFormatted =
        UserParametersFormatted(
            gender = parameters.gender,
            birthDate = DateFormatter.formatDate(parameters.birthDateTimestamp, DateFormatter.Pattern.DAY_MONTH_YEAR),
            birthDateTimestamp = parameters.birthDateTimestamp,
            weight = parameters.weight.toString(),
            wellnessGoal = parameters.wellnessGoal,
            useStatsPermission = parameters.useStatsPermission,
        )
}