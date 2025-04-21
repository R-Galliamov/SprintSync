package com.developers.sprintsync.domain.user_parameters.model

data class UserParameters(
    val gender: Gender,
    val birthDateTimestamp: Long,
    val weightKilos: Float,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
) {
    companion object {
        val DEFAULT =
            UserParameters(
                gender = Gender.MALE,
                birthDateTimestamp = 946684800L,
                weightKilos = 70.0f,
                wellnessGoal = WellnessGoal.MAINTAIN_WEIGHT,
                useStatsPermission = true,
            )
    }
}
