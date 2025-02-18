package com.developers.sprintsync.tracking.service.implementation

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.developers.sprintsync.tracking.component.model.TrackingStatus
import com.developers.sprintsync.tracking.service.controller.ServiceCommand.FINISH_SERVICE
import com.developers.sprintsync.tracking.service.controller.ServiceCommand.PAUSE_SERVICE
import com.developers.sprintsync.tracking.service.controller.ServiceCommand.START_SERVICE
import com.developers.sprintsync.tracking.service.notifier.TrackingServiceNotifier
import com.developers.sprintsync.tracking.data.flow.DistanceFlowManager
import com.developers.sprintsync.tracking.data.flow.DurationFlowManager
import com.developers.sprintsync.tracking.data.flow.LocationDurationFlowManager
import com.developers.sprintsync.tracking.service.manager.TrackingStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() { // TODO try LifecycleService()
    @Inject
    lateinit var notifier: TrackingServiceNotifier

    @Inject
    lateinit var trackingStateManager: TrackingStateManager

    @Inject
    lateinit var durationFlow: DurationFlowManager

    @Inject
    lateinit var distanceFlow: DistanceFlowManager

    @Inject
    lateinit var locationDurationFlow: LocationDurationFlowManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            START_SERVICE -> startTracking()
            PAUSE_SERVICE -> pauseTracking()
            FINISH_SERVICE -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTracking() {
        Log.i("My stack", "Service is started")
        startForegroundNotification()
        trackingStateManager.updateTrackingStatus(TrackingStatus.ACTIVE)

        locationDurationFlow.start(scope) { data ->
            trackingStateManager.updateLocationDuration(data)
        }

        durationFlow.start(scope) { notifier.updateDuration(it) }
        distanceFlow.start(scope) { notifier.updateDistance(it) }
    }

    private fun pauseTracking() {
        Log.i("My stack", "Service is paused")
        trackingStateManager.updateTrackingStatus(TrackingStatus.PAUSED)
        locationDurationFlow.stop()
        durationFlow.stop()
    }

    private fun stopTracking() {
        Log.i("My stack", "Service is stopped")
        locationDurationFlow.stop()
        trackingStateManager.updateTrackingStatus(TrackingStatus.COMPLETED)
        durationFlow.clean()
        stopSelf()
    }

    private fun startForegroundNotification() {
        val id = TrackingServiceNotifier.NOTIFICATION_ID
        val foregroundServiceType = notifier.notification.build()
        startForeground(
            id,
            foregroundServiceType,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d("My stack", "Service onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder = Binder()
}
