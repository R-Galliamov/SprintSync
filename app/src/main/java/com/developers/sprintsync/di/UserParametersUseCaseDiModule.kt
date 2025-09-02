package com.developers.sprintsync.di

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.user_profile.UserParametersRepository
import com.developers.sprintsync.domain.user_profile.use_case.SaveUserParameters
import com.developers.sprintsync.domain.user_profile.use_case.UserParametersValidator
import com.developers.sprintsync.domain.user_profile.use_case.UserParamsLimits
import com.developers.sprintsync.domain.user_profile.use_case.UserParamsPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate

@Module
@InstallIn(SingletonComponent::class)
class ProvideUserParametersUseCaseDiModule {

    @Provides
    fun provideSaveUserParameters(
        repo: UserParametersRepository,
        validator: UserParametersValidator,
        log: AppLogger,
    ): SaveUserParameters {
        return SaveUserParameters(repo, validator, log)
    }

    @Provides
    fun provideUserParametersValidator(
        policy: UserParamsPolicy,
        log: AppLogger
    ): UserParametersValidator {
        val todayProvider: () -> LocalDate = { LocalDate.now() }
        return UserParametersValidator(policy, todayProvider, log)
    }

    @Provides
    fun provideUserParametersPolicy(): UserParamsPolicy {
        val upl = UserParamsLimits
        return UserParamsPolicy(
            weightRangeKg = upl.MIN_WEIGHT_KG..upl.MAX_WEIGHT_KG,
            maxAgeYears = upl.MAX_AGE_YEARS,
        )
    }
}