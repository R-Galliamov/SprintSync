package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.DeleteTrackByIdUseCase
import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.GetTrackByIdUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TrackDetailsViewModel
    @Inject
    constructor(
        private val getTrackByIdUseCase: GetTrackByIdUseCase,
        private val deleteTrackByIdUseCase: DeleteTrackByIdUseCase,
    ) : ViewModel() {
        fun getTrackById(id: Int): LiveData<Track?> =
            liveData {
                withContext(Dispatchers.IO) {
                    val track = getTrackByIdUseCase.invoke(id)
                    emit(track)
                }
            }

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                deleteTrackByIdUseCase(id)
            }
        }
    }
