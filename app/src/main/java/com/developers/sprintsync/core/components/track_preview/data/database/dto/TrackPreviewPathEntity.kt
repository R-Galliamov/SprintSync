package com.developers.sprintsync.core.components.track_preview.data.database.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewPath

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = [TrackPreviewColumnKeys.PREVIEW_ID],
            childColumns = [TrackPreviewColumnKeys.TRACK_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
)
data class TrackPreviewPathEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val trackId: Int,
    val timestamp: Long,
    val filePath: String?,
) {
    fun toDto(): TrackPreviewPath {
        require(this.filePath != null) { "filePath must not be null" }
        val id = this.id
        val trackId = this.trackId
        val timestamp = this.timestamp
        val filePath = this.filePath
        return TrackPreviewPath(id, trackId, timestamp, filePath)
    }

    companion object {
        fun fromDto(dto: TrackPreviewPath) =
            TrackPreviewPathEntity(
                id = dto.id,
                trackId = dto.trackId,
                timestamp = dto.timestamp,
                filePath = dto.filePath,
            )
    }
}

fun List<TrackPreviewPathEntity>.toDto() = map(TrackPreviewPathEntity::toDto)
