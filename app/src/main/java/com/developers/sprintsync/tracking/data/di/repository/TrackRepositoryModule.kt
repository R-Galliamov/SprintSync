package com.developers.sprintsync.tracking.data.di.repository

import com.developers.sprintsync.tracking.data.repository.TrackRepository
import com.developers.sprintsync.tracking.data.repository.TrackRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository
}
