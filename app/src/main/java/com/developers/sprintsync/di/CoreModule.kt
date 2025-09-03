package com.developers.sprintsync.di

import com.developers.sprintsync.data.components.IdGenerator
import com.developers.sprintsync.data.components.ULIIdGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    fun provideIdGenerator(): IdGenerator = ULIIdGenerator()
}