package com.developers.sprintsync.di

import com.developers.sprintsync.manager.location.LocationProvider
import com.developers.sprintsync.manager.location.LocationProviderImpl
import com.developers.sprintsync.util.stopwatch.Stopwatch
import com.developers.sprintsync.util.stopwatch.StopwatchImpl
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
