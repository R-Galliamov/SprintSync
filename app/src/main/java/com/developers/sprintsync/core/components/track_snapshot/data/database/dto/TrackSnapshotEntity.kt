package com.developers.sprintsync.core.components.track_snapshot.data.database.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = [TrackSnapshotColumnKeys.SNAPSHOT_ID],
            childColumns = [TrackSnapshotColumnKeys.TRACK_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
data class TrackSnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val trackId: Int,
    val timestamp: Long,
    val filePath: String?,
) {
    companion object {
        fun fromDto(
            dto: TrackSnapshot,
            bitmapPath: String,
        ) = TrackSnapshotEntity(
            id = dto.id,
            trackId = dto.trackId,
            timestamp = dto.timestamp,
            filePath = bitmapPath,
        )
    }
}
