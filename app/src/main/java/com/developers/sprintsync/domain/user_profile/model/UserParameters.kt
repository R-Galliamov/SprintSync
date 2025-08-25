package com.developers.sprintsync.domain.user_profile.model

data class UserParameters(
    val sex: Sex,
    val birthDateTimestamp: Long,
    val weightKg: Float,
) {

    companion object {
        val DEFAULT =
            UserParameters(
                sex = Sex.MALE,
                birthDateTimestamp = 946684800L,
                weightKg = 70.0f,
            )
    }
}
