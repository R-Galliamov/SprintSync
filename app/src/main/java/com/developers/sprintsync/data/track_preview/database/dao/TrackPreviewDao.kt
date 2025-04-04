package com.developers.sprintsync.data.track_preview.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.developers.sprintsync.data.track_preview.database.dto.TrackPreviewPathEntity
import com.developers.sprintsync.data.track_preview.database.dto.TrackPreviewWrapperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPreviewDao {
    @Insert
    suspend fun insertPreview(entity: TrackPreviewPathEntity)

    @Query("SELECT * FROM TrackPreviewPathEntity ORDER BY id DESC")
    suspend fun getAllTrackPreviewPaths() : List<TrackPreviewPathEntity>

    @Transaction
    @Query("SELECT * FROM TrackEntity WHERE id = :trackId")
    fun getTrackPreviewWrapper(trackId: Int): TrackPreviewWrapperEntity

    @Transaction
    @Query("SELECT * FROM TrackEntity ORDER BY timestamp DESC")
    fun getAllTrackPreviewWrappers(): Flow<List<TrackPreviewWrapperEntity>>
}
