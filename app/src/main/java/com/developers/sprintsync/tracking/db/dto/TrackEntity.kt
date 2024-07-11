package com.developers.sprintsync.tracking.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.developers.sprintsync.tracking.model.track.Segments
import com.developers.sprintsync.tracking.model.track.Track

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val durationMillis: Long,
    val distanceMeters: Int,
    val avgPace: Float,
    val bestPace: Float,
    val calories: Int,
    val segments: Segments,
) {
    fun toDto() =
        Track(
            id = id,
            timestamp = timestamp,
            durationMillis = durationMillis,
            distanceMeters = distanceMeters,
            avgPace = avgPace,
            bestPace = bestPace,
            calories = calories,
            segments = segments,
        )

    companion object {
        fun fromDto(dto: Track) =
            TrackEntity(
                id = dto.id,
                timestamp = dto.timestamp,
                durationMillis = dto.durationMillis,
                distanceMeters = dto.distanceMeters,
                avgPace = dto.avgPace,
                bestPace = dto.bestPace,
                calories = dto.calories,
                segments = dto.segments,
            )
    }
}

fun List<TrackEntity>.toDto() = map(TrackEntity::toDto)

fun List<Track>.toEntity() = map(TrackEntity::fromDto)
