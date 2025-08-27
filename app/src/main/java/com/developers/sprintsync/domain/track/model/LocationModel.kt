package com.developers.sprintsync.domain.track.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

@JvmInline
value class Latitude(
    val value: Double,
)

@JvmInline
value class Longitude(
    val value: Double,
)

data class LocationModel(
    val lat: Latitude,
    val lon: Longitude,
)

fun Location.toDataModel(): LocationModel {
    val latitude = this.latitude
    val longitude = this.longitude
    return LocationModel(
        lat = Latitude(latitude),
        lon = Longitude(longitude),
    )
}

fun LocationModel.toLatLng(): LatLng = LatLng(lat.value, lon.value)
