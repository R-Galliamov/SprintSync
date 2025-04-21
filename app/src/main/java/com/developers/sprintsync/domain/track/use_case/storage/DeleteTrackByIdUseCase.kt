package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.data.track.repository.TrackRepository
import javax.inject.Inject

class DeleteTrackByIdUseCase
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
    ) {
        suspend operator fun invoke(trackId: Int) = trackRepository.deleteTrackById(trackId)
    }
