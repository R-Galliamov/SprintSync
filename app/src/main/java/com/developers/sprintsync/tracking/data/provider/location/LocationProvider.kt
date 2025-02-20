package com.developers.sprintsync.tracking.data.provider.location

import com.developers.sprintsync.tracking.data.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun locationFlow(): Flow<LocationModel>
}
