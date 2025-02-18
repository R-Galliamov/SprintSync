package com.developers.sprintsync.core.components.track.domain.use_case

import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.domain.util.validator.TrackValidator
import com.developers.sprintsync.core.util.validation.ValidationResult
import javax.inject.Inject

class ValidateTrackUseCase
    @Inject
    constructor() {
        operator fun invoke(track: Track): ValidationResult = TrackValidator.validate(track)
    }
