package com.developers.sprintsync.data.track_preview.source.file.name_generator

import javax.inject.Inject

class TrackPreviewFileNameGenerator
    @Inject
    constructor() {
        fun generateName(
            trackId: Int,
            timestamp: Long,
        ): String = FILE_NAME_TEMPLATE.format(trackId, timestamp)

        companion object {
            private const val FILE_NAME_TEMPLATE = "snapshot_%d_%d.jpg"
        }
    }
