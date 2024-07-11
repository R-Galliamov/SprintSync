package com.developers.sprintsync.tracking.session.di

import com.developers.sprintsync.tracking.session.service.manager.TrackingSessionManager
import com.developers.sprintsync.tracking.session.service.manager.TrackingSessionManagerImpl
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
    abstract fun bindTrackingSessionManager(impl: TrackingSessionManagerImpl): TrackingSessionManager
}
