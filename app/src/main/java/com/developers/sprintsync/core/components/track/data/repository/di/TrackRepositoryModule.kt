package com.developers.sprintsync.core.components.track.data.repository.di

import com.developers.sprintsync.core.components.track.data.repository.TrackRepository
import com.developers.sprintsync.core.components.track.data.repository.TrackRepositoryTest
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
    abstract fun bindTrackRepository(impl: TrackRepositoryTest): TrackRepository
}
