package com.developers.sprintsync.core.components.track_snapshot.data.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity

data class TrackWithSnapshot(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
    )
    val snapshot: TrackSnapshotEntity,
)