package com.developers.sprintsync.di

import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.manager.location.LocationManagerImpl
import com.developers.sprintsync.manager.locationModel.LocationModelManager
import com.developers.sprintsync.manager.locationModel.LocationModelManagerImpl
import com.developers.sprintsync.util.stopwatch.Stopwatch
import com.developers.sprintsync.util.stopwatch.StopwatchImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationManagerModule() {
    @Binds
    @Singleton
    abstract fun bindLocationManager(impl: LocationManagerImpl): LocationManager
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StopWatchModule() {

    @Binds
    abstract fun bindStopWatch(impl: StopwatchImpl): Stopwatch
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModelManagerModule() {
    @Binds
    @Singleton
    abstract fun bindLocationModelManager(impl: LocationModelManagerImpl): LocationModelManager
}
