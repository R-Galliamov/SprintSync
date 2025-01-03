package com.developers.sprintsync.statistics.data.repository.di

import com.developers.sprintsync.statistics.data.repository.dailyGoal.DailyGoalRepository
import com.developers.sprintsync.statistics.data.repository.dailyGoal.DailyGoalRepositoryTest
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DailyGoalRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDailyGoalRepository(impl: DailyGoalRepositoryTest): DailyGoalRepository
}
