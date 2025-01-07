package com.developers.sprintsync.tracking_session.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.map.components.useCase.GetMinimalMapStyleUseCase
import com.developers.sprintsync.map.components.useCase.GetUnlabeledMapStyleUseCase
import com.developers.sprintsync.core.components.track.domain.use_case.SaveTrackUseCase
import com.developers.sprintsync.core.tracking_service.data.model.session.TrackStatus
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.tracking_service.manager.TrackingSessionManager
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingSessionViewModel
    @Inject
    constructor(
        getMinimalMapStyleUseCase: GetMinimalMapStyleUseCase,
        getUnlabeledMapStyleUseCase: GetUnlabeledMapStyleUseCase,
        private val sessionManager: TrackingSessionManager,
        private val saveTrackUseCase: SaveTrackUseCase,
    ) : ViewModel() {
        val minimalMapStyle: MapStyleOptions = getMinimalMapStyleUseCase.invoke()

        val unlabeledMapStyle: MapStyleOptions = getUnlabeledMapStyleUseCase.invoke()

        val trackerState = sessionManager.trackSessionOrchestratorState.asLiveData()

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

        fun saveTrack(track: Track) {
            viewModelScope.launch {
                saveTrackUseCase.invoke(track)
            }
        }

        fun onDestroy() {
            if (sessionManager.data.value.trackStatus !is TrackStatus.Incomplete) {
                sessionManager.reset()
            }
        }
    }
