package com.developers.sprintsync.core.components.track_snapshot.data.repository

import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot

interface TrackSnapshotRepository {
    suspend fun saveSnapshot(snapshot: TrackSnapshot)
}
