package com.developers.sprintsync.data.goal.data_source.di

import com.developers.sprintsync.data.goal.data_source.DailyGoalDataSource
import com.developers.sprintsync.data.goal.data_source.InMemoryDailyGoalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DailyGoalDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindDailyGoalDataSource(impl: InMemoryDailyGoalDataSource): DailyGoalDataSource
}
