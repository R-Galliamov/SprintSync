package com.developers.sprintsync.core.components.goal.data.repository.di

import com.developers.sprintsync.core.components.goal.data.repository.DailyGoalRepository
import com.developers.sprintsync.core.components.goal.data.repository.TestDailyGoalRepository
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
    abstract fun bindDailyGoalRepository(impl: TestDailyGoalRepository): DailyGoalRepository
}
