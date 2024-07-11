package com.developers.sprintsync.tracking.dataStorage.di.db

import com.developers.sprintsync.tracking.dataStorage.db.AppDb
import com.developers.sprintsync.tracking.dataStorage.db.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModules {
    @Provides
    fun providePostDao(db: AppDb): TrackDao = db.trackDao()
}
