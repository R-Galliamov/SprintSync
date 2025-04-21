package com.developers.sprintsync.data.track.service.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ServiceComponent::class)
object TrackingServiceModule {
    @Provides
    fun provideTrackingCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
}
