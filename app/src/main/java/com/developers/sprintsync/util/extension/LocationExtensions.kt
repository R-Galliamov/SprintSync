package com.developers.sprintsync.util.extension

import android.location.Location
import com.developers.sprintsync.model.Latitude
import com.developers.sprintsync.model.LocationModel
import com.developers.sprintsync.model.Longitude

fun Location.toDataModel(): LocationModel =
    LocationModel(Latitude(latitude), Longitude(longitude))