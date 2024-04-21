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
        val trackerState = repository.trackerState.asLiveData()
        val data = repository.data.asLiveData()
    }
