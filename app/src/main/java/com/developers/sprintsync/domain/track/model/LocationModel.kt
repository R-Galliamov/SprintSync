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

fun Location.toDataModel(): LocationModel = LocationModel(Latitude(latitude), Longitude(longitude))

fun LocationModel.toLatLng(): LatLng = LatLng(lat.value, lon.value)

fun LocationModel.distanceBetweenInMeters(end: LocationModel): Float {
    val result = FloatArray(1)
    Location.distanceBetween(
        this.lat.value,
        this.lon.value,
        end.lat.value,
        end.lon.value,
        result,
    )
    return result[0]
}
