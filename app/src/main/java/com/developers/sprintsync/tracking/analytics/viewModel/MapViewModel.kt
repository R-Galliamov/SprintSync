package com.developers.sprintsync.tracking.analytics.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.developers.sprintsync.tracking.dataStorage.repository.useCase.GetTrackByIdUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val getTrackByIdUseCase: GetTrackByIdUseCase,
    ) : ViewModel() {
        fun getTrackById(id: Int): LiveData<Track?> =
            liveData {
                withContext(Dispatchers.IO) {
                    val track = getTrackByIdUseCase.invoke(id)
                    emit(track)
                }
            }
    }
