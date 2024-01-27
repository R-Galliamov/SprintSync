package com.developers.sprintsync.util.`typealias`

import com.developers.sprintsync.model.LocationModel

typealias Track = List<List<LocationModel>>
typealias MutableTrack = MutableList<List<LocationModel>>

fun MutableTrack.addPoint(point: LocationModel) {
    if (this.isEmpty()) this.add(emptyList())
    val index = this.lastIndex
    val currentList = this[index].toMutableList()
    currentList.add(point)
    this[index] = currentList.toList()
}