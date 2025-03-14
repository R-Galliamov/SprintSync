package com.developers.sprintsync.core.components.track_preview.data.model

import com.developers.sprintsync.core.components.track.data.model.Track

data class TrackPreviewWrapper(
    val track: Track,
    val preview: TrackPreviewPath?,
)
