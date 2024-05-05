package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel
    @Inject
    constructor(private val repository: TrackingRepository) : ViewModel() {
        val trackerState = repository.trackerState.asLiveData()

        val currentLocation =
            repository.data.map { it.currentLocation }.distinctUntilChanged().asLiveData()
        val track = repository.data.map { it.track }.distinctUntilChanged().asLiveData()
        val duration = repository.data.map { it.durationMillis }.distinctUntilChanged().asLiveData()
    }
