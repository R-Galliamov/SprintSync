package com.developers.sprintsync.domain.tracking_service.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object TrackingServiceModule {
    @Provides
    fun provideTrackingCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
}
