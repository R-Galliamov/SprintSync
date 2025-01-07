package com.developers.sprintsync.map.components.useCase

import com.developers.sprintsync.map.data.model.MapStyle
import com.developers.sprintsync.map.data.repository.MapStyleRepository
import com.google.android.gms.maps.model.MapStyleOptions
import javax.inject.Inject

class GetDetailedMapStyleUseCase
    @Inject
    constructor(
        private val mapStyleRepository: MapStyleRepository,
    ) {
        fun invoke(): MapStyleOptions = mapStyleRepository.getMapStyle(MapStyle.DETAILED)
    }
