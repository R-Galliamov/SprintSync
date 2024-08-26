package com.developers.sprintsync.tracking.dataStorage.di.repository

import com.developers.sprintsync.tracking.dataStorage.repository.track.TrackRepository
import com.developers.sprintsync.tracking.dataStorage.repository.track.TestRepository
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
    abstract fun bindTrackRepository(impl: TestRepository): TrackRepository
}
