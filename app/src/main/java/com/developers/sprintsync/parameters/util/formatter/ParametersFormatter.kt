package com.developers.sprintsync.parameters.util.formatter

import com.developers.sprintsync.global.util.formatter.DateFormatter
import com.developers.sprintsync.parameters.model.UserParameters
import com.developers.sprintsync.parameters.model.UserParametersFormatted

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