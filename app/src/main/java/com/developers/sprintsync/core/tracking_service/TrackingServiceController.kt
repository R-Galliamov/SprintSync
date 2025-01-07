package com.developers.sprintsync.core.tracking_service

import android.content.Context
import android.content.Intent

class TrackingServiceController(
    private val context: Context,
) {
    fun startService() {
        sendCommandToService(Action.START_SERVICE)
    }

    fun pauseService() {
        sendCommandToService(Action.PAUSE_SERVICE)
    }

    fun finish() {
        sendCommandToService(Action.FINISH_SERVICE)
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
        const val FINISH_SERVICE = "STOP_SERVICE"
    }
}
