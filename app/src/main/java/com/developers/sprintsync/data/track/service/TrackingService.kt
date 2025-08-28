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
import com.developers.sprintsync.data.track.service.ServiceCommand.STOP_LOCATION_UPDATES
import com.developers.sprintsync.data.track.service.processing.session.SessionManager
import com.developers.sprintsync.data.track.service.processing.session.TrackingController
import com.developers.sprintsync.data.track.service.processing.session.TrackingDataManager
import com.developers.sprintsync.domain.track.model.SessionData
import com.developers.sprintsync.domain.track.model.TrackingData
import com.developers.sprintsync.domain.track.model.TrackingStatus
import com.developers.sprintsync.presentation.workout_session.notification.TrackingNotificationConfig
import com.developers.sprintsync.presentation.workout_session.notification.TrackingNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Defines constants for intent actions to control [TrackingService] operations.
 */
object ServiceCommand {
    const val LAUNCH_LOCATION_UPDATES = "LAUNCH_LOCATION_UPDATES"
    const val STOP_LOCATION_UPDATES = "STOP_LOCATION_UPDATES"
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
    scope: CoroutineScope,
    log: AppLogger
) {
    private val serviceActive = MutableStateFlow(false)

    val trackingDataFlow: StateFlow<TrackingData> =
        trackingDataManager.trackingDataFlow.combine(serviceActive) { d, a ->
            if (d.status == TrackingStatus.Completed || a) d else null
        }.filterNotNull().stateIn(scope, SharingStarted.WhileSubscribed(), TrackingData.INITIAL)

    val sessionDataFlow: StateFlow<SessionData> = sessionManager.sessionDataFlow.emitIfActive(serviceActive)
        .stateIn(scope, SharingStarted.WhileSubscribed(), SessionData.INITIAL)

    fun activate() {
        serviceActive.value = true
    }

    fun deactivate() {
        serviceActive.value = false
    }

    init {
        log.i("TrackingServiceDataHolder HashCode: ${this.hashCode()} - INIT")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Flow<T>.emitIfActive(active: StateFlow<Boolean>): Flow<T> =
        active.flatMapLatest { isActive -> if (isActive) this else emptyFlow() }
}

/**
 * Manages tracking operations as a foreground service
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
    lateinit var dataHolder: TrackingServiceDataHolder

    @Inject
    lateinit var log: AppLogger

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        log.i("HashCode: ${this.hashCode()} - ON CREATE. DataHolder HashCode: ${dataHolder.hashCode()}")
    }

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
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            LAUNCH_LOCATION_UPDATES -> launchLocationUpdates()
            STOP_LOCATION_UPDATES -> stopLocationUpdates()
            START_SERVICE -> startTracking()
            PAUSE_SERVICE -> stop()
            FINISH_SERVICE -> finish()
        }
        return START_STICKY
    }

    // Initiates location updates
    private fun launchLocationUpdates() {
        try {
            dataHolder.activate()
            trackingController.startLocationUpdates()
            log.i("Location updates launched")
        } catch (e: Exception) {
            log.e("Failed to launch location updates: ${e.message}", e)
        }
    }

    // Stops location updates
    private fun stopLocationUpdates() {
        try {
            trackingController.stopLocationUpdates()
            log.i("Location updates stopped")
        } catch (e: Exception) {
            log.e("Failed to stopped location updates: ${e.message}", e)
        }
    }

    // Starts or resumes tracking and foreground service with notification data updates
    private fun startTracking() {
        try {
            dataHolder.activate()
            startForegroundNotification()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    launch {
                        startNotificationUpdates()
                    }
                    trackingController.start()
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

    // Stops tracking
    private fun stop() {
        try {
            trackingController.stops()
            log.i("Service paused")
        } catch (e: Exception) {
            log.e("Failed to pause tracking: ${e.message}", e)
        }
    }

    // Finishes tracking and service
    private fun finish() {
        try {
            trackingController.finish()
            dataHolder.deactivate()
            trackingController.reset()
            stopSelf()
            log.i("Service stopped")
        } catch (e: Exception) {
            log.e("Failed to stop tracking: ${e.message}", e)
        }
    }

    // Starts the foreground notification
    private fun startForegroundNotification() {
        val id = notificationConfig.notificationId
        try {
            startForeground(id, notification)
            log.i("Foreground notification started with ID: $id")
        } catch (e: Exception) {
            log.e("Failed to start foreground notification with ID: $id")
        }

    }

    private suspend fun startNotificationUpdates() {
        try {
            notificationManager.launchUpdates()
        } catch (e: Exception) {
            log.e("Failed to run notification updates: ${e.message}", e)
        }
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
