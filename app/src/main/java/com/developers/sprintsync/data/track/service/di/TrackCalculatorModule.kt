package com.developers.sprintsync.data.track.service.di

import com.developers.sprintsync.domain.track.use_case.service.ITrackCalculator
import com.developers.sprintsync.domain.track.use_case.service.TrackCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
object TrackCalculatorModule {
    @Provides
    fun provideTrackCalculator(): ITrackCalculator = TrackCalculator()
}
