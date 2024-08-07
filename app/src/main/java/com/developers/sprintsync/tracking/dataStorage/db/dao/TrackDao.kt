package com.developers.sprintsync.tracking.dataStorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.tracking.dataStorage.db.dto.TrackEntity
import com.developers.sprintsync.tracking.session.model.track.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM TrackEntity WHERE id = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("SELECT * FROM TrackEntity WHERE id = :id")
    fun getTrackById(id: Int): Track

    @Query("SELECT * FROM TrackEntity ORDER BY id DESC")
    fun getAllTracks(): Flow<List<Track>>
}
