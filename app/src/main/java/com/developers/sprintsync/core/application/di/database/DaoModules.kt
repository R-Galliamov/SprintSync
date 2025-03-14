package com.developers.sprintsync.core.application.di.database

import com.developers.sprintsync.core.database.AppDb
import com.developers.sprintsync.core.components.track.data.database.dao.TrackDao
import com.developers.sprintsync.core.components.goal.data.database.dao.DailyGoalDao
import com.developers.sprintsync.core.components.track_preview.data.database.dao.TrackPreviewDao
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
