package com.developers.sprintsync.domain.track_preview.use_case

import com.developers.sprintsync.domain.track_preview.model.TrackPreviewBitmap
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import javax.inject.Inject

class SaveTrackPreviewUseCase
    @Inject
    constructor(
        private val repository: TrackPreviewRepository,
    ) {
        suspend operator fun invoke(bitmap: TrackPreviewBitmap) = repository.savePreview(bitmap)
    }
