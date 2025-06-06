package com.developers.sprintsync.data.track.service

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.ServiceCommand.FINISH_SERVICE
import com.developers.sprintsync.data.track.service.ServiceCommand.LAUNCH_LOCATION_UPDATES
import com.developers.sprintsync.data.track.service.ServiceCommand.PAUSE_SERVICE
import com.developers.sprintsync.data.track.service.ServiceCommand.START_SERVICE
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationConfig
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationManager
import com.developers.sprintsync.data.track.service.processing.session.SessionManager
import com.developers.sprintsync.data.track.service.processing.session.TrackingController
import com.developers.sprintsync.data.track.service.processing.session.TrackingDataManager
import com.developers.sprintsync.domain.track.model.SessionData
import com.developers.sprintsync.domain.track.model.TrackingData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Defines constants for intent actions to control [TrackingService] operations.
 */
object ServiceCommand {
    const val LAUNCH_LOCATION_UPDATES = "LAUNCH_LOCATION_UPDATES"
    const val START_SERVICE = "START_SERVICE"
    const val PAUSE_SERVICE = "PAUSE_SERVICE"
    const val FINISH_SERVICE = "FINISH_SERVICE"
}

/**
 * Holds tracking and session data flows for the service
 */
@ServiceScoped
class TrackingServiceDataHolder
@Inject
constructor(
    trackingDataManager: TrackingDataManager,
    sessionManager: SessionManager,
) {
    val trackingDataFlow: Flow<TrackingData> = trackingDataManager.trackingDataFlow
    val sessionDataFlow: Flow<SessionData> = sessionManager.sessionDataFlow
}

/**
 * Manages tracking operations as a foreground service
 * TODO add error handling
 */
@AndroidEntryPoint
class TrackingService : LifecycleService() {
    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var notificationConfig: TrackingNotificationConfig

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var trackingController: TrackingController

    @Inject
    lateinit var data: TrackingServiceDataHolder

    @Inject
    lateinit var log: AppLogger

    private val binder = LocalBinder()

    /**
     * Handles incoming intents to control tracking operations.
     * Assumes the intent's action is one of [ServiceCommand] constants (e.g., [ServiceCommand.START_SERVICE]).
     * @param intent The intent containing the command (e.g., start, pause, stop).
     * @param flags Additional flags for the intent.
     * @param startId A unique ID for this start request.
     * @return The service start behavior.
     */
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            LAUNCH_LOCATION_UPDATES -> launchLocationUpdates()
            START_SERVICE -> startTracking()
            PAUSE_SERVICE -> pauseTracking()
            FINISH_SERVICE -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Initiates location updates
    private fun launchLocationUpdates() {
        try {
            trackingController.startLocationUpdates()
            log.i("Location updates launched")
        } catch (e: Exception) {
            log.e("Failed to launch location updates: ${e.message}", e)
        }
    }

    // Starts tracking and foreground service
    private fun startTracking() {
        try {
            startForegroundNotification()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    trackingController.startTracking()
                    log.i("Tracking started in coroutine")
                } catch (e: Exception) {
                    log.e("Failed to start tracking in coroutine: ${e.message}", e)
                }
            }
            log.i("Service started with foreground notification")
        } catch (e: Exception) {
            log.e("Failed to start foreground notification: ${e.message}", e)
        }
    }

    // Pauses tracking
    private fun pauseTracking() {
        try {
            trackingController.pauseTracking()
            log.i("Service paused")
        } catch (e: Exception) {
            log.e("Failed to pause tracking: ${e.message}", e)
        }
    }

    // Stops tracking and service
    private fun stopTracking() {
        try {
            trackingController.stopTracking()
            stopSelf()
            log.i("Service stopped")
        } catch (e: Exception) {
            log.e("Failed to stop tracking: ${e.message}", e)
        }
    }

    // Starts the foreground notification
    private fun startForegroundNotification() {
        val id = notificationConfig.notificationId
        startForeground(id, notification)
        log.i("Foreground notification started with ID: $id")
    }

    // Binder for service interaction
    inner class LocalBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    /**
     * Binds the service to clients.
     * @param intent The intent used to bind the service.
     * @return The IBinder for service communication.
     */
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        log.i("Service bound")
        return binder
    }
}
