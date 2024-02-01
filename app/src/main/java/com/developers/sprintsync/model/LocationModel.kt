package com.developers.sprintsync.model

@JvmInline
value class Latitude(val value: Double)

@JvmInline
value class Longitude(val value: Double)

data class LocationModel(
    val latitude: Latitude,
    val longitude: Longitude,
)