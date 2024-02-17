package com.developers.sprintsync.model.tracking

import android.location.Location

@JvmInline
value class Latitude(val value: Double)

@JvmInline
value class Longitude(val value: Double)

data class LocationModel(
    val latitude: Latitude,
    val longitude: Longitude,
)

fun Location.toDataModel(): LocationModel =
    LocationModel(Latitude(latitude), Longitude(longitude))