package com.developers.sprintsync.domain.track_preview.model

import com.developers.sprintsync.domain.track.model.Track

data class TrackPreviewWrapper(
    val track: Track,
    val preview: TrackPreviewPath?,
)
