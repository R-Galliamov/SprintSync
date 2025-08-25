package com.developers.sprintsync.core.database.di

import com.developers.sprintsync.core.database.AppDb
import com.developers.sprintsync.data.track.database.dao.TrackDao
import com.developers.sprintsync.data.track_preview.source.database.dao.TrackPreviewDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModules {
    @Provides
    fun provideTrackDao(db: AppDb): TrackDao = db.trackDao()

    @Provides
    fun provideTrackPreviewDao(db: AppDb) : TrackPreviewDao = db.trackPreviewDao()
}
