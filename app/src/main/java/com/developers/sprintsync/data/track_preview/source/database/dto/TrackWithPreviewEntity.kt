package com.developers.sprintsync.data.track_preview.source.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.data.track_preview.model.TrackWithPreview

data class TrackWithPreviewEntity(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = TrackPreviewColumnKeys.PREVIEW_ID,
        entityColumn = TrackPreviewColumnKeys.TRACK_ID,
    )
    val preview: TrackPreviewEntity?,
) {
    fun toDto(): TrackWithPreview = TrackWithPreview(track = this.track.toDto(), preview = this.preview?.toDto())
}

fun List<TrackWithPreviewEntity>.toDto() = map(TrackWithPreviewEntity::toDto)
