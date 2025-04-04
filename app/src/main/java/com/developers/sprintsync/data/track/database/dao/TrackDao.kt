package com.developers.sprintsync.data.track.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert
    suspend fun insertTrack(track: TrackEntity) : Long

    @Query("DELETE FROM TrackEntity WHERE id = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("SELECT * FROM TrackEntity WHERE id = :id")
    suspend fun getTrackById(id: Int): Track

    @Query("SELECT * FROM TrackEntity ORDER BY id DESC")
    fun getAllTracks(): Flow<List<Track>>
}
