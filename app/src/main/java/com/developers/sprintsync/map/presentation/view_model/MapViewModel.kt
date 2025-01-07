package com.developers.sprintsync.map.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.developers.sprintsync.map.components.useCase.GetDetailedMapStyleUseCase
import com.developers.sprintsync.core.components.track.domain.use_case.GetTrackByIdUseCase
import com.developers.sprintsync.core.components.track.data.model.Track
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val getTrackByIdUseCase: GetTrackByIdUseCase,
        getDetailedMapStyleUseCase: GetDetailedMapStyleUseCase,
    ) : ViewModel() {
        val detailedMapStyle: MapStyleOptions = getDetailedMapStyleUseCase.invoke()

        fun getTrackById(id: Int): LiveData<Track?> =
            liveData {
                withContext(Dispatchers.IO) {
                    val track = getTrackByIdUseCase.invoke(id)
                    emit(track)
                }
            }
    }
