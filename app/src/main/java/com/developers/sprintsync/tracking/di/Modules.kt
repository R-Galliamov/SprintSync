package com.developers.sprintsync.tracking.di

import com.developers.sprintsync.tracking.service.provider.location.LocationProvider
import com.developers.sprintsync.tracking.service.provider.location.LocationProviderImpl
import com.developers.sprintsync.tracking.service.provider.time.stopwatch.Stopwatch
import com.developers.sprintsync.tracking.service.provider.time.stopwatch.StopwatchImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationProviderModule {
    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StopwatchModule {
    @Binds
    abstract fun bindStopwatch(impl: StopwatchImpl): Stopwatch
}
