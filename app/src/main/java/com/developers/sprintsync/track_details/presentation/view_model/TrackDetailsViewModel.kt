package com.developers.sprintsync.track_details.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.developers.sprintsync.core.components.track.domain.use_case.DeleteTrackByIdUseCase
import com.developers.sprintsync.core.components.track.domain.use_case.GetTrackByIdUseCase
import com.developers.sprintsync.core.components.track.data.model.Track
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
                } // TODO update with flow and UiTrack
            }

        fun deleteTrackById(id: Int) {
            viewModelScope.launch {
                deleteTrackByIdUseCase(id)
            }
        }
    }
