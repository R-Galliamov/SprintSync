package com.developers.sprintsync.domain.di

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.validator.SegmentValidator
import com.developers.sprintsync.domain.track.validator.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {

    @Provides
    fun provideSegmentValidator(): Validator<Segment> = SegmentValidator()
}
