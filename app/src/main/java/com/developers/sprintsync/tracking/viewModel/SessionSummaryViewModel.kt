package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionSummaryViewModel
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) : ViewModel() {
        val lastTrack: LiveData<Track?> =
            repository.tracks
                .map {
                    it.firstOrNull()
                }.asLiveData()

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                repository.deleteTrackById(id)
            }
        }
    }
