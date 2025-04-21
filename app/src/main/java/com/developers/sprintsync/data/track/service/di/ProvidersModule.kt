package com.developers.sprintsync.data.track.service.di

import com.developers.sprintsync.data.track.service.provider.DurationProvider
import com.developers.sprintsync.data.track.service.provider.DurationProviderImpl
import com.developers.sprintsync.data.track.service.provider.LocationProvider
import com.developers.sprintsync.data.track.service.provider.LocationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
abstract class LocationProviderModule {
    @Binds
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider
}

@Module
@InstallIn(ServiceComponent::class)
abstract class DurationProviderModule {
    @Binds
    abstract fun bindDurationProvider(impl: DurationProviderImpl): DurationProvider
}
