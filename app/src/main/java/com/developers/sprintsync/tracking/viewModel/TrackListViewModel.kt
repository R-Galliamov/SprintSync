package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
    ) : ViewModel() {
        val tracks = trackRepository.tracks.asLiveData()
    }
