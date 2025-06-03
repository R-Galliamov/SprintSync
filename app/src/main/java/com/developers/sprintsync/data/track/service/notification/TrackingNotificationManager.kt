package com.developers.sprintsync.data.track.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.withLatestConcat
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.data.track.service.TrackingServiceDataHolder
import com.developers.sprintsync.presentation.components.DistanceFormatter
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Implementation is provided via DI
abstract class TrackingNotificationConfig {
    abstract val channelId: String
    abstract val channelName: String
    abstract val notificationId: Int
}

data class TrackingNotificationData(
    val distanceMeters: Float,
    val durationMillis: Long,
)

abstract class TrackingNotificationDataProvider {
    abstract val dataFlow: Flow<TrackingNotificationData>
}

@ServiceScoped
class TrackingNotificationDataProviderImpl @Inject constructor(serviceData: TrackingServiceDataHolder) :
    TrackingNotificationDataProvider() {

    private val distanceFlow: Flow<Float> = serviceData.trackingDataFlow.map { it.track.distanceMeters }
    private val durationFlow: Flow<Long> = serviceData.sessionDataFlow.map { it.durationMillis }

    override val dataFlow: Flow<TrackingNotificationData> =
        distanceFlow.withLatestConcat(durationFlow) { distance, duration ->
            TrackingNotificationData(distanceMeters = distance, durationMillis = duration)
        }
}

data class TrackingNotificationDisplayModel(
    val duration: String,
    val distance: String,
)

class NotificationLayoutUpdater @Inject constructor(val layout: RemoteViews) {
    fun update(data: TrackingNotificationDisplayModel) {
        layout.setTextViewText(R.id.tvDurationValue, data.duration)
        layout.setTextViewText(R.id.tvDistanceValue, data.distance)
    }
}

class TrackingNotificationDisplayMapper @Inject constructor(
    private val distanceFormatter: DistanceFormatter,
) {
    fun map(data: TrackingNotificationData): TrackingNotificationDisplayModel {
        val duration = DurationUiFormatter.format(data.durationMillis, DurationUiPattern.HH_MM_SS)
        val distance = distanceFormatter.format(data.distanceMeters).value
        return TrackingNotificationDisplayModel(duration = duration, distance = distance)
    }
}

@ServiceScoped
class TrackingNotificationManager
@Inject
constructor(
    private val notificationData: TrackingNotificationDataProvider,
    private val notificationManager: NotificationManager,
    private val notificationLayoutUpdater: NotificationLayoutUpdater,
    private val notificationBuilder: NotificationCompat.Builder,
    private val displayMapper: TrackingNotificationDisplayMapper,
    private val config: TrackingNotificationConfig,
    private val log: AppLogger,
) {

    private var lastNotificationData: TrackingNotificationDisplayModel? = null

    init {
        createNotificationChannel()
    }

    suspend fun launchUpdates() {
        notificationData.dataFlow.map { displayMapper.map(it) }.collect { data ->
            if (data == lastNotificationData) return@collect
            lastNotificationData = data
            updateNotification(data)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(config.channelId, config.channelName, IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        log.i("Notification channel created: id=${config.channelId}, name=${config.channelName}")
    }

    private fun updateNotification(data: TrackingNotificationDisplayModel) {
        notificationLayoutUpdater.update(data)
        notificationBuilder.setCustomContentView(notificationLayoutUpdater.layout)
        notificationManager.notify(config.notificationId, notificationBuilder.build())
    }
}
