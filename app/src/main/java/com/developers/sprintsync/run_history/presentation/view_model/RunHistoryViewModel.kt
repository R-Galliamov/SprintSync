package com.developers.sprintsync.run_history.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.core.components.track.domain.use_case.GetTracksFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunHistoryViewModel
    @Inject
    constructor(
        getTracksFlowUseCase: GetTracksFlowUseCase,
    ) : ViewModel() {
        val tracks = getTracksFlowUseCase.tracks.asLiveData()
    }
