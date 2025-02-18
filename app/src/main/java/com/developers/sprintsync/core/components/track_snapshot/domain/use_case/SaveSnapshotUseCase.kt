package com.developers.sprintsync.core.components.track_snapshot.domain.use_case

import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot
import com.developers.sprintsync.core.components.track_snapshot.data.repository.TrackSnapshotRepository
import javax.inject.Inject

class SaveSnapshotUseCase
    @Inject
    constructor(
        private val repository: TrackSnapshotRepository,
    ) {
        suspend operator fun invoke(snapshot: TrackSnapshot) = repository.saveSnapshot(snapshot)
    }
