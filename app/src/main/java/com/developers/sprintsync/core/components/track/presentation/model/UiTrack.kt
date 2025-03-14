package com.developers.sprintsync.core.components.track.presentation.model

import com.google.android.gms.maps.model.PolylineOptions

data class UiTrack(
    val id: Int,
    val distance: String,
    val duration: String,
    val avgPace: String,
    val bestPace: String,
    val calories: String,
    val polylines: List<PolylineOptions>,
)