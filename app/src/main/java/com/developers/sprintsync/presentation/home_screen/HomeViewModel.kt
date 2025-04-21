package com.developers.sprintsync.presentation.home_screen

import androidx.lifecycle.ViewModel
import com.developers.sprintsync.domain.track.use_case.storage.GetTracksFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getTracksFlowUseCase: GetTracksFlowUseCase,
    ) : ViewModel() {
        val statistics: Flow<WorkoutStatistics> =
            getTracksFlowUseCase.tracks.map { tracks -> WorkoutStatistics.create(tracks) }
    }
