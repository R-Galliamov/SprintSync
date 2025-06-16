package com.developers.sprintsync.presentation.workout_session.notification

import com.developers.sprintsync.data.track.service.TrackingServiceDataHolder
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ServiceScoped
class TrackingNotificationDataProviderImpl @Inject constructor(serviceData: TrackingServiceDataHolder) :
    TrackingNotificationDataProvider() {

    private val distanceFlow: Flow<Float> = serviceData.trackingDataFlow.map { it.track.distanceMeters }
    private val durationFlow: Flow<Long> = serviceData.sessionDataFlow.map { it.durationMillis }

    override val dataFlow: Flow<TrackingNotificationData> =
        distanceFlow.combine(durationFlow) { distance, duration ->
            TrackingNotificationData(distanceMeters = distance, durationMillis = duration)
        }
}