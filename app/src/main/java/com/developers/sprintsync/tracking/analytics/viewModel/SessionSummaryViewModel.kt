package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.DeleteTrackByIdUseCase
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetLastTrackUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
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
