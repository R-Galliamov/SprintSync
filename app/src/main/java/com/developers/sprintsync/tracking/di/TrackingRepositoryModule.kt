package com.developers.sprintsync.tracking.di

import com.developers.sprintsync.tracking.repository.TrackingRepository
import com.developers.sprintsync.tracking.repository.TrackingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTrackingRepository(impl: TrackingRepositoryImpl): TrackingRepository
}
