package com.developers.sprintsync.core.components.track_snapshot.data.data_source.di

import com.developers.sprintsync.core.components.track_snapshot.data.data_source.TrackSnapshotDataSource
import com.developers.sprintsync.core.components.track_snapshot.data.data_source.TrackSnapshotFileDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackSnapshotDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindTrackSnapshotDataSource(impl: TrackSnapshotFileDataSource): TrackSnapshotDataSource
}
