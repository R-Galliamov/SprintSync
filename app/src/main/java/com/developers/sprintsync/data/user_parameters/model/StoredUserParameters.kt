package com.developers.sprintsync.data.user_parameters.model

import com.developers.sprintsync.domain.user_profile.model.Sex
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import java.time.LocalDate

data class StoredUserParameters(
    val sexCode: String? = null,
    val birthDate: String? = null,
    val weightKg: Float? = null
)

fun StoredUserParameters?.toDomain(): UserParameters? {
    if (this == null) return null
    val def = UserParameters.DEFAULT
    return UserParameters(
        sex = sexCode?.let { runCatching { Sex.fromCode(it) }.getOrElse { def.sex } } ?: def.sex,
        birthDate = birthDate?.let { LocalDate.parse(it) } ?: def.birthDate,
        weightKg = (weightKg ?: def.weightKg)
    )
}

fun UserParameters.toStored(): StoredUserParameters {
    return StoredUserParameters(
        sexCode = this.sex.code,
        birthDate = this.birthDate.toString(),
        weightKg = this.weightKg
    )
}