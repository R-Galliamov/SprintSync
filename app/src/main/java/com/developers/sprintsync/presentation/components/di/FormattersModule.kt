package com.developers.sprintsync.presentation.components.di

import com.developers.sprintsync.presentation.components.DistanceFormatter
import com.developers.sprintsync.presentation.components.KilometerFormatter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FormattersModule {

    @Binds
    @Singleton
    abstract fun bindDistanceFormatter(impl: KilometerFormatter): DistanceFormatter
}