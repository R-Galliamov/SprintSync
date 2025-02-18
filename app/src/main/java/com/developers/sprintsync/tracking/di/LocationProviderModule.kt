package com.developers.sprintsync.tracking.di

import com.developers.sprintsync.tracking.data.provider.location.LocationProvider
import com.developers.sprintsync.tracking.data.provider.location.LocationProviderImpl
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
