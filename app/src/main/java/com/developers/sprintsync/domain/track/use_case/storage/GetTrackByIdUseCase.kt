package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.data.track.repository.TrackRepository
import javax.inject.Inject

class GetTrackByIdUseCase
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
    ) {
        suspend operator fun invoke(trackId: Int) = trackRepository.getTrackById(trackId)
    }
