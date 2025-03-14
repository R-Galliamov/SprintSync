package com.developers.sprintsync.core.components.track_preview.data.data_source.di

import com.developers.sprintsync.core.components.track_preview.data.data_source.TrackPreviewDataSource
import com.developers.sprintsync.core.components.track_preview.data.data_source.TrackPreviewFileDataSource
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
