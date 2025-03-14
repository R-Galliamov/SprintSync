package com.developers.sprintsync.core.components.track_preview.data.database.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewWrapper

data class TrackPreviewWrapperEntity(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = TrackPreviewColumnKeys.PREVIEW_ID,
        entityColumn = TrackPreviewColumnKeys.TRACK_ID,
    )
    val preview: TrackPreviewPathEntity?,
) {
    fun toDto(): TrackPreviewWrapper = TrackPreviewWrapper(track = this.track.toDto(), preview = this.preview?.toDto())
}

fun List<TrackPreviewWrapperEntity>.toDto() = map(TrackPreviewWrapperEntity::toDto)
