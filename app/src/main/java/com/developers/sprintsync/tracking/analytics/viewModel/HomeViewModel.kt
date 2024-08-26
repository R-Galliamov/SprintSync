package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.analytics.dataManager.builder.TrackStatisticsCompactFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.TrackingStatisticsFormatter
import com.developers.sprintsync.tracking.analytics.model.FormattedStatistics
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetTracksFlowUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getTracksFlowUseCase: GetTracksFlowUseCase,
    ) : ViewModel() {
        val statistics =
            getTracksFlowUseCase.tracks.map { tracks -> formatStatistics(tracks) }.asLiveData()

        private fun formatStatistics(tracks: List<Track>): FormattedStatistics {
            val stats = TrackStatisticsCompactFormatter().formatCompactStatistics(tracks)
            return TrackingStatisticsFormatter.format(stats)
        }
    }
