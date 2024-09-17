package com.developers.sprintsync.global.dataStorage.di

import com.developers.sprintsync.global.dataStorage.db.AppDb
import com.developers.sprintsync.tracking.dataStorage.db.dao.TrackDao
import com.developers.sprintsync.statistics.dataStorage.db.dao.DailyGoalDao
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
}
