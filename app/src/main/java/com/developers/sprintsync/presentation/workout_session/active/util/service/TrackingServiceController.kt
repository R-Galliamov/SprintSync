package com.developers.sprintsync.presentation.workout_session.active.util.service

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.ServiceCommand
import com.developers.sprintsync.data.track.service.TrackingService
import com.developers.sprintsync.data.track.service.TrackingServiceDataHolder
import com.developers.sprintsync.data.track.service.TrackingServiceException

/**
 * Result of a service connection attempt.
 */
sealed class ServiceConnectionResult {
    /**
     * Successful connection with the service data holder.
     * @param dataHolder The [TrackingServiceDataHolder] from the service.
     */
    data class Success(
        val dataHolder: TrackingServiceDataHolder,
    ) : ServiceConnectionResult()

    /**
     * Failed connection with the exception.
     * @param e The [TrackingServiceException] that occurred.
     */
    data class Failure(
        val e: TrackingServiceException,
    ) : ServiceConnectionResult()
}

/**
 * Controls binding and communication with [TrackingService].
 */
class TrackingServiceController(
    private val context: Context,
    private val log: AppLogger,
) {
    private var isBound = false
    private var serviceDataHolder: TrackingServiceDataHolder? = null
    private lateinit var connectionCallback: (ServiceConnectionResult) -> Unit

    private val connection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                binder: IBinder?,
            ) {
                val localBinder = binder as? TrackingService.LocalBinder
                val service = localBinder?.getService()
                val data = service?.dataHolder

                if (data != null) {
                    isBound = true
                    serviceDataHolder = data
                    connectionCallback(ServiceConnectionResult.Success(data))
                    log.i("Service connected, data holder initialized")
                } else {
                    isBound = false
                    val exception = TrackingServiceException.BindingFailed()
                    connectionCallback(ServiceConnectionResult.Failure(exception))
                    log.e("Service binding failed: ${exception.message}", exception)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                serviceDataHolder = null
                val exception = TrackingServiceException.ServiceDisconnected()
                connectionCallback(ServiceConnectionResult.Failure(exception))
                log.e("Service disconnected: ${exception.message}")
            }
        }

    /**
     * Binds to [TrackingService] and provides connection result.
     * @param activity The [Activity] to bind the service to.
     * @param onConnectionResult Callback for the [ServiceConnectionResult].
     */
    fun bind(
        activity: Activity,
        onConnectionResult: (ServiceConnectionResult) -> Unit,
    ) {
        if (isBound && serviceDataHolder != null) {
            serviceDataHolder?.let {
                onConnectionResult(ServiceConnectionResult.Success(it))
                log.d("Already bound, returning existing data holder")
            }
            return
        }

        connectionCallback = onConnectionResult
        val intent = Intent(context, TrackingService::class.java)
        isBound = activity.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        if (!isBound) {
            val exception = TrackingServiceException.BindingFailed()
            onConnectionResult(ServiceConnectionResult.Failure(exception))
            log.e("Failed to bind service: ${exception.message}")
        } else {
            log.d("Binding to TrackingService")
        }
    }

    // Unbinds from the service if currently bound
    fun unbindService(activity: Activity) {
        if (isBound) {
            activity.unbindService(connection)
            isBound = false
            serviceDataHolder = null
            log.i("Service unbound")
        }
    }

    // Sends command to launch location updates
    fun launchLocationUpdates() = sendCommand(ServiceCommand.LAUNCH_LOCATION_UPDATES)

    // Sends command to stop location updates
    fun stopLocationUpdates() = sendCommand(ServiceCommand.STOP_LOCATION_UPDATES)

    // Sends command to start the service
    fun startService() = sendCommand(ServiceCommand.START_SERVICE)

    // Sends command to pause the service
    fun pauseService() = sendCommand(ServiceCommand.PAUSE_SERVICE)

    // Sends command to finish the service
    fun finishService() = sendCommand(ServiceCommand.FINISH_SERVICE)

    // Sends a command to the service via intent
    private fun sendCommand(command: String) {
        val intent =
            Intent(context, TrackingService::class.java).apply {
                action = command
            }
        context.startService(intent)
        log.d("Sent command: $command")
    }
}
