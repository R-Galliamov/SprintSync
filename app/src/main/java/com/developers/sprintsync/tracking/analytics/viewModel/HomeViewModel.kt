package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.analytics.dataManager.builder.TrackingStatsBuilder
import com.developers.sprintsync.tracking.dataStorage.repository.useCase.GetAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getAllTracksUseCase: GetAllTracksUseCase,
    ) : ViewModel() {
        val statistics =
            getAllTracksUseCase.tracks.map { TrackingStatsBuilder.buildStats(it) }.asLiveData()
    }
