package com.developers.sprintsync.core.database.di

import com.developers.sprintsync.core.database.AppDb
import com.developers.sprintsync.data.track.database.dao.TrackDao
import com.developers.sprintsync.data.goal.source.database.dao.DailyGoalDao
import com.developers.sprintsync.data.track_preview.database.dao.TrackPreviewDao
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
    fun provideDailyGoalDao(db: AppDb): DailyGoalDao = db.dailyGoalDao()

    @Provides
    fun provideTrackPreviewDao(db: AppDb) : TrackPreviewDao = db.trackPreviewDao()
}
