package com.developers.sprintsync.data.track_preview.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.developers.sprintsync.data.track_preview.source.database.dto.TrackPreviewEntity
import com.developers.sprintsync.data.track_preview.source.database.dto.TrackWithPreviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPreviewDao {
    @Insert
    suspend fun insertPreview(entity: TrackPreviewEntity)

    @Query("SELECT * FROM TrackPreviewEntity ORDER BY id DESC")
    suspend fun getAllTrackPreviewPaths() : List<TrackPreviewEntity>

    @Transaction
    @Query("SELECT * FROM TrackEntity WHERE id = :trackId")
    fun getTrackWithPreview(trackId: Int): TrackWithPreviewEntity

    @Transaction
    @Query("SELECT * FROM TrackEntity ORDER BY timestamp DESC")
    fun getAllTracksWithPreviews(): Flow<List<TrackWithPreviewEntity>>
}
