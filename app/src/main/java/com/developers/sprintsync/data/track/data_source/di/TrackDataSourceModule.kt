package com.developers.sprintsync.data.track.data_source.di

import com.developers.sprintsync.data.track.data_source.InMemoryTrackDataSource
import com.developers.sprintsync.data.track.data_source.TrackDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindTrackDataSource(impl: InMemoryTrackDataSource): TrackDataSource
}
