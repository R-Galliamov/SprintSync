package com.developers.sprintsync.data.track.repository.di

import com.developers.sprintsync.data.track.repository.TrackRepositoryImpl
import com.developers.sprintsync.data.track.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackDataRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository
}
