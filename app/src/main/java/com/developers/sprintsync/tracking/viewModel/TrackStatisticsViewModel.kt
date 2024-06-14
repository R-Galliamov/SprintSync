package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TrackStatisticsViewModel
    @Inject
    constructor(private val repository: TrackRepository) : ViewModel() {
        val track: LiveData<Track> =
            repository.tracks.map {
                it.first()
            }.asLiveData()
    }
