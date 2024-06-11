package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.service.manager.TrackingSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TrackingSessionViewModel
    @Inject
    constructor(
        private val sessionManager: TrackingSessionManager,
    ) : ViewModel() {
        val trackerState = sessionManager.trackerState.asLiveData()

        val currentLocation =
            sessionManager.data.map { it.currentLocation }.distinctUntilChanged().asLiveData()
        val track = sessionManager.data.map { it.track }.distinctUntilChanged().asLiveData()
        val duration = sessionManager.data.map { it.durationMillis }.distinctUntilChanged().asLiveData()
    }
