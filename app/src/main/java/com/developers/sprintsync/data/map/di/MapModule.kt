package com.developers.sprintsync.data.map.di

import com.developers.sprintsync.data.map.GoogleMapStyle
import com.developers.sprintsync.data.map.TrackPreviewStyle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MapModule {

    @Provides
    @Singleton
    fun provideTrackPreviewStyle(): TrackPreviewStyle =
        TrackPreviewStyle(padding = 100, mapStyle = GoogleMapStyle.UNLABELED)
}