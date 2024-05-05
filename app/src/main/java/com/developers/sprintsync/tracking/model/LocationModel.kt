package com.developers.sprintsync.tracking.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng

@JvmInline
value class Latitude(val value: Double)

@JvmInline
value class Longitude(val value: Double)

data class LocationModel(
    val latitude: Latitude,
    val longitude: Longitude,
)

fun Location.toDataModel(): LocationModel = LocationModel(Latitude(latitude), Longitude(longitude))

fun LocationModel.toLatLng(): LatLng = LatLng(latitude.value, longitude.value)

fun LocationModel.distanceBetweenInMeters(end: LocationModel): Float {
    val result = FloatArray(1)
    Location.distanceBetween(
        this.latitude.value,
        this.longitude.value,
        end.latitude.value,
        end.longitude.value,
        result,
    )
    return result[0]
}
