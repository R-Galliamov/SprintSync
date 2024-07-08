package com.developers.sprintsync.tracking.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TrackHistoryStatisticsViewModel
    @Inject
    constructor(
        private val repository: TrackRepository,
    ) : ViewModel() {
        fun getTrackById(id: Int): LiveData<Track?> =
            liveData {
                withContext(Dispatchers.IO) {
                    val track = repository.getTrackById(id)
                    emit(track)
                }
            }

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                repository.deleteTrackById(id)
            }
        }
    }
