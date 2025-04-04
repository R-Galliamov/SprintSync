package com.developers.sprintsync.domain.track.use_case

import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.validation.track.TrackValidator
import javax.inject.Inject

class ValidateTrackUseCase
    @Inject
    constructor() {
        operator fun invoke(track: Track): Track = TrackValidator.validateOrThrow(track)
    }
