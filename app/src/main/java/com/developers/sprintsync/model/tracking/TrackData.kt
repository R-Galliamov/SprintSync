package com.developers.sprintsync.model.tracking

typealias Track = List<GeoTimePair>
typealias MutableTrack = MutableList<List<GeoTimePair>>

data class TrackData(
    val id: Int,
    val durationMillis: Long,
    val distanceMeters: Int,
    val track: Track,
)
