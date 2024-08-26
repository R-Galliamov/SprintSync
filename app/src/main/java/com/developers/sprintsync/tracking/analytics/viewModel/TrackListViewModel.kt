package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetTracksFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel
    @Inject
    constructor(
        getTracksFlowUseCase: GetTracksFlowUseCase,
    ) : ViewModel() {
        val tracks = getTracksFlowUseCase.tracks.asLiveData()
    }
