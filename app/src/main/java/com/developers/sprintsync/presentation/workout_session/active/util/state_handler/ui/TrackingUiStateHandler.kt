package com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui

import com.developers.sprintsync.tracking.component.model.TrackingStatus
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ViewModelScoped
class TrackingUiStateHandler
    @Inject
    constructor() {
        private val _uiStateFlow = MutableStateFlow<UIState?>(null)
        val uiStateFlow get() = _uiStateFlow.asStateFlow().filterNotNull()

        fun handleState(status: TrackingStatus) {
            val newState =
                when (status) {
                    TrackingStatus.INITIALIZED -> UIState.Initialized
                    TrackingStatus.ACTIVE -> UIState.Active
                    TrackingStatus.PAUSED -> UIState.Paused
                    TrackingStatus.COMPLETED -> UIState.Completing
                }

            if (newState != _uiStateFlow.value) {
                _uiStateFlow.update { newState }
            }
        }
    }
