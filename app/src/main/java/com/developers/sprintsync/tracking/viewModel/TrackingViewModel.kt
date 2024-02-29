package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel
    @Inject
    constructor(private val repository: TrackingRepository) : ViewModel() {
        val isTracking = repository.isTracking.asLiveData()
        val track = repository.track.asLiveData()
        val timeMillis = repository.timeMillis.asLiveData()
    }
