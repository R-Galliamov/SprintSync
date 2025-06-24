package com.developers.sprintsync.domain.user_profile.model

data class UserParameters(
    val sex: Sex,
    val birthDateTimestamp: Long,
    val weightKg: Float,
    val wellnessGoal: WellnessGoal,
    val useStatsPermission: Boolean,
) {

    companion object {
        val DEFAULT =
            UserParameters(
                sex = Sex.MALE,
                birthDateTimestamp = 946684800L,
                weightKg = 70.0f,
                wellnessGoal = WellnessGoal.MAINTAIN_WEIGHT,
                useStatsPermission = true,
            )
    }
}
