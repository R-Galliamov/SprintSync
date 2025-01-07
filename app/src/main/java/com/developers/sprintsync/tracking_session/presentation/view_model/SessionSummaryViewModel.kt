package com.developers.sprintsync.tracking_session.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track.domain.use_case.DeleteTrackByIdUseCase
import com.developers.sprintsync.core.components.track.domain.use_case.GetLastTrackUseCase
import com.developers.sprintsync.core.components.track.data.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionSummaryViewModel
    @Inject
    constructor(
        getLastTrackUseCase: GetLastTrackUseCase,
        private val deleteTrackByIdUseCase: DeleteTrackByIdUseCase,
    ) : ViewModel() {
        val lastTrack: LiveData<Track?> = getLastTrackUseCase().asLiveData()

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                deleteTrackByIdUseCase(id)
            }
        }
    }
