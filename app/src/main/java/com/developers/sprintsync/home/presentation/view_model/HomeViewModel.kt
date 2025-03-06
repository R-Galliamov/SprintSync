package com.developers.sprintsync.home.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.domain.use_case.GetTracksFlowUseCase
import com.developers.sprintsync.home.statistics.domain.util.creator.TracksStatsCompactCreator
import com.developers.sprintsync.home.statistics.presentation.model.TracksStatsCompactFormatted
import com.developers.sprintsync.home.statistics.presentation.util.formatter.TracksStatsFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getTracksFlowUseCase: GetTracksFlowUseCase,
        private val tracksStatsFormatter: TracksStatsFormatter,
    ) : ViewModel() {
        val statistics =
            getTracksFlowUseCase.tracks.map { tracks -> formatStatistics(tracks) }.asLiveData()

        private fun formatStatistics(tracks: List<Track>): TracksStatsCompactFormatted {
            val stats = TracksStatsCompactCreator().createTracksStats(tracks)
            return tracksStatsFormatter.format(stats)
        }
    }
