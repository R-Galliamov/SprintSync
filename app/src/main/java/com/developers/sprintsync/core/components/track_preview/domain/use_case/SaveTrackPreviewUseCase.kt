package com.developers.sprintsync.core.components.track_preview.domain.use_case

import com.developers.sprintsync.core.components.track_preview.data.model.TrackPreviewBitmap
import com.developers.sprintsync.core.components.track_preview.data.repository.TrackPreviewRepository
import javax.inject.Inject

class SaveTrackPreviewUseCase
    @Inject
    constructor(
        private val repository: TrackPreviewRepository,
    ) {
        suspend operator fun invoke(bitmap: TrackPreviewBitmap) = repository.savePreview(bitmap)
    }
