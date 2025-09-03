package com.developers.sprintsync.domain.user_profile.use_case

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.core.Resource
import com.developers.sprintsync.domain.core.AppResult
import com.developers.sprintsync.domain.user_profile.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SaveUserParameters(
    private val repo: UserParametersRepository,
    private val validator: UserParametersValidator,
    private val log: AppLogger,
) {
    suspend operator fun invoke(params: UserParameters): AppResult<Unit?, UserParamsError> {
        val errors = validator.validate(params)
        if (errors.isNotEmpty()) {
            log.w("User params validation failed: $errors")
            return AppResult.Failure.Validation(errors)
        }
        return when (val r = repo.save(params)) {
            Resource.Result.Empty,
            is Resource.Result.Success<*> -> AppResult.Success(Unit)

            is Resource.Result.Error -> r.throwable.toUnexpected()
        }.also { log.d("SaveUserParameters result: $it") }
    }
}

private fun Throwable.toUnexpected(): AppResult.Failure.Unexpected = when (this) {
    is kotlinx.coroutines.CancellationException -> throw this
    is Exception -> AppResult.Failure.Unexpected(this)
    else -> AppResult.Failure.Unexpected(IllegalStateException("Non-Exception throwable: $this"))
}


object UserParamsLimits {
    const val MIN_WEIGHT_KG = 15f
    const val MAX_WEIGHT_KG = 300f
    const val MAX_AGE_YEARS = 120
}

data class UserParamsPolicy(
    val weightRangeKg: ClosedFloatingPointRange<Float>,
    val maxAgeYears: Int,
)

enum class UserParamsError { INVALID_BIRTHDATE, INVALID_WEIGHT }

class UserParametersValidator(
    private val policy: UserParamsPolicy,
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
    private val log: AppLogger,
) {

    fun validate(p: UserParameters): Set<UserParamsError> {
        val errors = mutableSetOf<UserParamsError>()
        val today = todayProvider()

        val birth = p.birthDate
        val invalidBirth = birth.isAfter(today) || ChronoUnit.YEARS.between(today, birth) > policy.maxAgeYears
        if (invalidBirth) errors.add(UserParamsError.INVALID_BIRTHDATE)

        val weight = p.weightKg
        val invalidWeight = weight !in policy.weightRangeKg
        if (invalidWeight) errors.add(UserParamsError.INVALID_WEIGHT)

        return errors.also {
            log.d("validate: $it")
        }
    }
}
