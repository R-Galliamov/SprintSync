package com.developers.sprintsync.data.goal.di

import com.developers.sprintsync.data.goal.repository.DailyGoalRepository
import com.developers.sprintsync.data.goal.repository.DailyGoalRepositoryImpl
import com.developers.sprintsync.data.goal.source.DailyGoalDataSource
import com.developers.sprintsync.data.goal.source.LocalDailyGoalDataSource
import com.developers.sprintsync.data.goal.source.database.dao.DailyGoalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoalStorageModule {
    @Provides
    @Singleton
    @Named(LOCAL_STORAGE_KEY)
    fun provideLocalDataSource(dao: DailyGoalDao): DailyGoalDataSource = LocalDailyGoalDataSource(dao)

    @Provides
    @Singleton
    fun provideDailyGoalRepository(
        @Named(LOCAL_STORAGE_KEY) localDataSource: DailyGoalDataSource,
    ): DailyGoalRepository = DailyGoalRepositoryImpl(localDataSource)

    private companion object {
        const val LOCAL_STORAGE_KEY = "local"
    }
}
