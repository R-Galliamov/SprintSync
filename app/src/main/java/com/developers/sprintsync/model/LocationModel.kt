package com.developers.sprintsync.model

@JvmInline
value class Latitude(val latitude: Double)

@JvmInline
value class Longitude(val longitude: Double)

data class LocationModel(
    val latitude: Latitude,
    val longitude: Longitude,
)