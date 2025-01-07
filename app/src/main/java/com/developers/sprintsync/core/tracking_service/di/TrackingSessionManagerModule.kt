package com.developers.sprintsync.core.tracking_service.di

import com.developers.sprintsync.core.tracking_service.manager.TrackingSessionManager
import com.developers.sprintsync.core.tracking_service.manager.DefaultTrackingSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingSessionManagerModule {
    @Binds
    @Singleton
    abstract fun bindTrackingSessionManager(impl: DefaultTrackingSessionManager): TrackingSessionManager
}
