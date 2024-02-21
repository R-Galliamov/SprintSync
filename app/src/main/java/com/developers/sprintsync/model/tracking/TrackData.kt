package com.developers.sprintsync.model.tracking

typealias Track = List<TrackSegment>
typealias MutableTrack = MutableList<List<TrackSegment>>

data class TrackData(
val id: Int,
val durationMillis: Long,
val distanceMeters: Int,
val track: Track,)
