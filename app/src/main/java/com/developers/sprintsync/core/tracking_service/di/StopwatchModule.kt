package com.developers.sprintsync.core.tracking_service.di

import com.developers.sprintsync.core.tracking_service.provider.time.stopwatch.Stopwatch
import com.developers.sprintsync.core.tracking_service.provider.time.stopwatch.StopwatchImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StopwatchModule {
    @Binds
    abstract fun bindStopwatch(impl: StopwatchImpl): Stopwatch
}
