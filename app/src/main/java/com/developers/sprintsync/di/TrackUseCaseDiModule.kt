package com.developers.sprintsync.di

import com.developers.sprintsync.domain.track.validator.TrackLimits
import com.developers.sprintsync.domain.track.validator.TrackValidationPolicy
import com.developers.sprintsync.domain.track.validator.TrackValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TrackUseCaseDiModule {

    @Provides
    fun provideTrackValidationPolicy(): TrackValidationPolicy {
        return TrackValidationPolicy(
            minTimestamp = TrackLimits.MIN_TIMESTAMP,
            minDuration = TrackLimits.MIN_DURATION,
            minDistance = TrackLimits.MIN_DISTANCE,
            minPace = TrackLimits.MIN_PACE,
            minCalories = TrackLimits.MIN_CALORIES,
            minSegments = TrackLimits.MIN_SEGMENTS,
        )
    }

    @Provides
    fun provideValidator(policy: TrackValidationPolicy): TrackValidator = TrackValidator(policy)
}