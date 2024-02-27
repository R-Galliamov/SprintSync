package com.developers.sprintsync.manager.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.Fragment
import com.developers.sprintsync.service.tracker.TrackingService

// TODO create with delegate or HILT?
class TrackingServiceManager(
    private val fragment: Fragment,
) {
    private val context = fragment.requireContext()

    private var _service: TrackingService? = null
    val service: TrackingService
        get() = checkNotNull(_service) { "Service isn't initialized" }

    private var _connection: ServiceConnection? = null
    private val connection get() = checkNotNull(_connection) { "Connection isn't initialized" }

    private fun setServiceConnection(onBind: () -> Unit) {
        _connection =
            object : ServiceConnection {
                override fun onServiceConnected(
                    className: ComponentName?,
                    iBinder: IBinder?,
                ) {
                    val binder = iBinder as TrackingService.ServiceBinder
                    _service = binder.getService()
                    onBind()
                }

                override fun onServiceDisconnected(className: ComponentName?) {
                    _service = null
                }
            }
    }

    fun bindService(onBind: () -> Unit) {
        setServiceConnection(onBind)
        val serviceIntent = Intent(context, TrackingService::class.java)
        fragment.requireActivity()
            .bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        fragment.requireActivity().unbindService(connection)
    }

    fun startService() {
        sendCommandToService(Action.START_SERVICE)
    }

    fun pauseService() {
        sendCommandToService(Action.PAUSE_SERVICE)
    }

    private fun sendCommandToService(action: String) {
        Intent(context, TrackingService::class.java).also { intent ->
            intent.action = action
            context.startService(intent)
        }
    }

    object Action {
        const val START_SERVICE = "START_SERVICE"
        const val PAUSE_SERVICE = "PAUSE_SERVICE"
        const val STOP_SERVICE = "STOP_SERVICE"
    }
}
