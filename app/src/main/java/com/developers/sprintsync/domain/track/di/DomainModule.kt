package com.developers.sprintsync.domain.track.di

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.use_case.storage.SaveTrackUseCase
import com.developers.sprintsync.domain.track.validator.SegmentValidator
import com.developers.sprintsync.domain.track.validator.TrackValidator
import com.developers.sprintsync.domain.track.validator.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrackModule {

    @Provides
    fun provideSegmentValidator(): Validator<Segment> = SegmentValidator()

    @Provides
    fun provideSaveTrackUseCase(
        validator: TrackValidator,
        repo: TrackRepository,
        log: AppLogger
    ) = SaveTrackUseCase(validator, repo, log)
}