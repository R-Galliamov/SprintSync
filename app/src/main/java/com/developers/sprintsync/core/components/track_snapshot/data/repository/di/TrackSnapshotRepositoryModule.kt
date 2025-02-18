package com.developers.sprintsync.core.components.track_snapshot.data.repository.di

import com.developers.sprintsync.core.components.track_snapshot.data.repository.TrackSnapshotRepository
import com.developers.sprintsync.core.components.track_snapshot.data.repository.TrackSnapshotRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackSnapshotRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTrackSnapshotRepository(impl: TrackSnapshotRepositoryImpl): TrackSnapshotRepository
}
