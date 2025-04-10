package com.developers.sprintsync.tracking.di

import com.developers.sprintsync.domain.tracking_service.internal.provider.DefaultDurationProvider
import com.developers.sprintsync.domain.tracking_service.internal.provider.DefaultLocationProvider
import com.developers.sprintsync.domain.tracking_service.internal.provider.DurationProvider
import com.developers.sprintsync.domain.tracking_service.internal.provider.LocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationProviderModule {
    @Binds
    abstract fun bindLocationProvider(impl: DefaultLocationProvider): LocationProvider
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DurationProviderModule {
    @Binds
    abstract fun bindDurationProvider(impl: DefaultDurationProvider): DurationProvider
}
