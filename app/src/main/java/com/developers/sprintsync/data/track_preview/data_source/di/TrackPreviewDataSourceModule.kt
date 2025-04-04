package com.developers.sprintsync.data.track_preview.data_source.di

import com.developers.sprintsync.data.track_preview.data_source.TrackPreviewDataSource
import com.developers.sprintsync.data.track_preview.data_source.TrackPreviewFileDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackPreviewDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindTrackPreviewDataSource(impl: TrackPreviewFileDataSource): TrackPreviewDataSource
}
