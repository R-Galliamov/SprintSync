package com.developers.sprintsync.tracking.dataStorage.repository.mapStyle.useCase

import com.developers.sprintsync.tracking.dataStorage.repository.mapStyle.MapStyle
import com.developers.sprintsync.tracking.dataStorage.repository.mapStyle.MapStyleRepository
import com.google.android.gms.maps.model.MapStyleOptions
import javax.inject.Inject

class GetMinimalMapStyleUseCase
    @Inject
    constructor(
        private val mapStyleRepository: MapStyleRepository,
    ) {
        fun invoke(): MapStyleOptions = mapStyleRepository.getMapStyle(MapStyle.MINIMAL)
    }
