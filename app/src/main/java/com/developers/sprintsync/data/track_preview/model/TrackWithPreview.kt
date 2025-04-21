package com.developers.sprintsync.data.track_preview.model

import com.developers.sprintsync.domain.track.model.Track

data class TrackWithPreview(
    val track: Track,
    val preview: TrackPreview?,
)
