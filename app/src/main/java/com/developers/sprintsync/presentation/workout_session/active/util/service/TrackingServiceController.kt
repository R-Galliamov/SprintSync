package com.developers.sprintsync.presentation.workout_session.active.util.service

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.developers.sprintsync.data.track.service.ServiceCommand
import com.developers.sprintsync.data.track.service.TrackingService
import com.developers.sprintsync.data.track.service.TrackingServiceDataHolder
import com.developers.sprintsync.data.track.service.TrackingServiceException

sealed class ServiceConnectionResult {
    data class Success(
        val dataHolder: TrackingServiceDataHolder,
    ) : ServiceConnectionResult()

    data class Failure(
        val e: TrackingServiceException,
    ) : ServiceConnectionResult()
}

class TrackingServiceController(
    private val context: Context,
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
                val data = service?.data

                if (data != null) {
                    isBound = true
                    serviceDataHolder = data
                    connectionCallback(ServiceConnectionResult.Success(data))
                } else {
                    isBound = false
                    connectionCallback(ServiceConnectionResult.Failure(TrackingServiceException.BindingFailed()))
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                serviceDataHolder = null
                connectionCallback(ServiceConnectionResult.Failure(TrackingServiceException.ServiceDisconnected()))
            }
        }

    fun bind(
        activity: Activity,
        onConnectionResult: (ServiceConnectionResult) -> Unit,
    ) {
        if (isBound && serviceDataHolder != null) {
            serviceDataHolder?.let { onConnectionResult(ServiceConnectionResult.Success(it)) }
            return
        }

        connectionCallback = onConnectionResult
        val intent = Intent(context, TrackingService::class.java)
        isBound = activity.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        if (!isBound) {
            onConnectionResult(ServiceConnectionResult.Failure(TrackingServiceException.BindingFailed()))
        }
    }

    fun unbindService(activity: Activity) {
        if (isBound) {
            activity.unbindService(connection)
            isBound = false
            serviceDataHolder = null
        }
    }

    fun launchLocationUpdates() = sendCommand(ServiceCommand.LAUNCH_LOCATION_UPDATES)

    fun startService() = sendCommand(ServiceCommand.START_SERVICE)

    fun pauseService() = sendCommand(ServiceCommand.PAUSE_SERVICE)

    fun finishService() = sendCommand(ServiceCommand.FINISH_SERVICE)

    private fun sendCommand(command: String) {
        val intent =
            Intent(context, TrackingService::class.java).apply {
                action = command
            }
        context.startService(intent)
    }
}
