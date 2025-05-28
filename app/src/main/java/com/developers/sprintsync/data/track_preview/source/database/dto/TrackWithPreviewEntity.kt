package com.developers.sprintsync.data.track_preview.source.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview

/**
 * Room data class representing a track and its associated preview, if available.
 */
data class TrackWithPreviewEntity(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = TrackPreviewColumnKeys.PREVIEW_ID,
        entityColumn = TrackPreviewColumnKeys.TRACK_ID,
    )
    val preview: TrackPreviewEntity?,
) {

    /**
     * Converts this entity to a [TrackWithPreview] domain model.
     * @return The converted [TrackWithPreview].
     */
    fun toDto(): TrackWithPreview = TrackWithPreview(track = this.track.toDto(), preview = this.preview?.toDto())
}

/**
 * Converts a list of [TrackWithPreviewEntity] to a list of [TrackWithPreview] domain models.
 * @return List of converted [TrackWithPreview] objects.
 */
fun List<TrackWithPreviewEntity>.toDto() = map(TrackWithPreviewEntity::toDto)
