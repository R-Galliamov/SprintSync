package com.developers.sprintsync.core.components.track_preview.data.repository.di

import com.developers.sprintsync.core.components.track_preview.data.repository.TrackPreviewRepository
import com.developers.sprintsync.core.components.track_preview.data.repository.TrackPreviewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackPreviewRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTrackPreviewRepository(impl: TrackPreviewRepositoryImpl): TrackPreviewRepository
}
