package com.developers.sprintsync.data.track.service.di

import com.developers.sprintsync.domain.track.use_case.service.SegmentGenerator
import com.developers.sprintsync.domain.track.use_case.service.SegmentGeneratorImpl
import com.developers.sprintsync.domain.user_parameters.use_case.UserParametersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(ServiceComponent::class)
object SegmentGeneratorModule {
    @Provides
    fun provideSegmentGenerator(useCase: UserParametersUseCase): SegmentGenerator {
        val userWeight = runBlocking { useCase().first().weightKilos }
        return SegmentGeneratorImpl(userWeight)
    }
}
