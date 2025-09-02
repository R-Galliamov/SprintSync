package com.developers.sprintsync.domain.user_profile.model

import java.time.LocalDate

data class UserParameters(
    val sex: Sex,
    val birthDate: LocalDate,
    val weightKg: Float,
) {

    companion object {
        val DEFAULT =
            UserParameters(
                sex = Sex.UNSPECIFIED,
                birthDate = LocalDate.ofYearDay(2000, 1),
                weightKg = 70.0f,
            )
    }
}
