package com.developers.sprintsync.tracking.data.di.db

import com.developers.sprintsync.tracking.data.db.AppDb
import com.developers.sprintsync.tracking.data.db.dao.TrackDao
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
