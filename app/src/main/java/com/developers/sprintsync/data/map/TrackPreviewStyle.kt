package com.developers.sprintsync.data.map

data class TrackPreviewStyle(
    val padding: Int,
    val mapStyle: GoogleMapStyle,
) {
    companion object {
        val DEFAULT = TrackPreviewStyle(
            padding = 100,
            mapStyle = GoogleMapStyle.UNLABELED
        )
    }
}
