package com.developers.sprintsync.tracking.dataStorage.di.repository

import com.developers.sprintsync.tracking.dataStorage.repository.TrackRepository
import com.developers.sprintsync.tracking.dataStorage.repository.TrackRepositoryImpl
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
