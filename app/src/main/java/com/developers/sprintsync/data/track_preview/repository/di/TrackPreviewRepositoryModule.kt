package com.developers.sprintsync.data.track_preview.repository.di

import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import com.developers.sprintsync.data.track_preview.repository.LocalFileDbTrackPreviewRepository
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
    abstract fun bindTrackPreviewRepository(impl: LocalFileDbTrackPreviewRepository): TrackPreviewRepository
}
