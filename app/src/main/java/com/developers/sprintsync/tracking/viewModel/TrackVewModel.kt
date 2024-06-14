package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackVewModel
    @Inject
    constructor(private val repository: TrackRepository) : ViewModel() {
        fun saveTrack(track: Track) {
            viewModelScope.launch {
                repository.saveTrack(track)
            }
        }

        fun getTracks() = repository.getAllTracks()
    }
