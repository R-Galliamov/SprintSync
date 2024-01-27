package com.developers.sprintsync.util.extension

import android.location.Location
import com.developers.sprintsync.model.LocationModel

fun Location.toDataModel(): LocationModel =
    LocationModel(this.latitude.toInt(), this.longitude.toInt())