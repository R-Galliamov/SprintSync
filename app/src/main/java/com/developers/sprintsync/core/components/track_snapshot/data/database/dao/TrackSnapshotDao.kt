package com.developers.sprintsync.core.components.track_snapshot.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.core.components.track_snapshot.data.database.dto.TrackSnapshotEntity

@Dao
interface TrackSnapshotDao {
    @Insert
    suspend fun insertSnapshot(snapshotEntity: TrackSnapshotEntity)

    @Query("DELETE FROM TrackSnapshotEntity WHERE trackId = :trackId")
    suspend fun deleteSnapshotByTrackId(trackId: Int)
}
