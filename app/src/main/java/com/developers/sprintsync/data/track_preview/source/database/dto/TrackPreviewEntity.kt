package com.developers.sprintsync.data.track_preview.source.database.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.data.track_preview.model.TrackPreview

/**
 * Room entity representing a track preview with a reference to its associated track.
 */
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
data class TrackPreviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val trackId: Int,
    val filePath: String?,
) {

    /**
     * Converts this entity to a [TrackPreview] domain model.
     * @return The converted [TrackPreview].
     * @throws IllegalArgumentException if [filePath] is null.
     */
    fun toDto(): TrackPreview {
        require(this.filePath != null) { "filePath must not be null" } // TODO handle error
        val id = this.id
        val trackId = this.trackId
        val filePath = this.filePath
        return TrackPreview(id = id, trackId = trackId, bitmapPath = filePath)
    }

    companion object {

        /**
         * Creates a [TrackPreviewEntity] from a [TrackPreview] domain model.
         * @param dto The [TrackPreview] to convert.
         * @return The corresponding [TrackPreviewEntity].
         */
        fun fromDto(dto: TrackPreview) =
            TrackPreviewEntity(
                id = dto.id,
                trackId = dto.trackId,
                filePath = dto.bitmapPath,
            )
    }
}