package com.developers.sprintsync.di

import com.developers.sprintsync.manager.location.LocationManager
import com.developers.sprintsync.manager.location.LocationManagerImpl
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