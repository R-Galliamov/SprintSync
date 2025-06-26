package com.developers.sprintsync.di

import com.developers.sprintsync.core.id_generator.IdGenerator
import com.developers.sprintsync.core.id_generator.ULIIdGenerator
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