package com.developers.sprintsync.tracking.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.tracking.db.dto.TrackEntity
import com.developers.sprintsync.tracking.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM TrackEntity WHERE id = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("SELECT * FROM TrackEntity ORDER BY id DESC")
    fun getAllTracks(): Flow<List<Track>>
}
