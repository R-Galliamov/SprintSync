package com.developers.sprintsync.tracking.session.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.dataStorage.repository.useCase.SaveTrackuseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.tracking.session.service.manager.TrackingSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingSessionViewModel
    @Inject
    constructor(
        private val sessionManager: TrackingSessionManager,
        private val saveTrackUseCase: SaveTrackuseCase,
    ) : ViewModel() {
        val trackerState = sessionManager.trackerState.asLiveData()

        val currentLocation =
            sessionManager.data
                .map { it.currentLocation }
                .distinctUntilChanged()
                .asLiveData()
        val track =
            sessionManager.data
                .map { it.track }
                .distinctUntilChanged()
                .asLiveData()
        val duration =
            sessionManager.data
                .map { it.durationMillis }
                .distinctUntilChanged()
                .asLiveData()

        val trackStatus =
            sessionManager.data
                .map { it.trackStatus }
                .distinctUntilChanged()
                .asLiveData()

        fun startUpdatingLocation() {
            sessionManager.startUpdatingLocation()
        }

        fun stopUpdatingLocation() {
            sessionManager.stopUpdatingLocation()
        }

        fun resetData() {
            sessionManager.reset()
        }

        fun saveTrack(track: Track) {
            viewModelScope.launch {
                saveTrackUseCase.invoke(track)
            }
        }
    }
