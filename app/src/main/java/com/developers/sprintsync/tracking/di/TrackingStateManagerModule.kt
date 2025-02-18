package com.developers.sprintsync.tracking.di

import com.developers.sprintsync.tracking.service.manager.TrackingStateManagerImpl
import com.developers.sprintsync.tracking.service.manager.TrackingStateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingStateManagerModule {
    @Binds
    abstract fun bindTrackingStateManager(impl: TrackingStateManagerImpl): TrackingStateManager
}
