package com.developers.sprintsync.tracking.service.controller

import android.content.Context
import android.content.Intent
import com.developers.sprintsync.tracking.service.implementation.TrackingService

class TrackingServiceController(
    private val context: Context,
) {
    fun startService() {
        sendCommandToService(ServiceCommand.START_SERVICE)
    }

    fun pauseService() {
        sendCommandToService(ServiceCommand.PAUSE_SERVICE)
    }

    fun finish() {
        sendCommandToService(ServiceCommand.FINISH_SERVICE)
    }

    private fun sendCommandToService(action: String) {
        Intent(context, TrackingService::class.java).also { intent ->
            intent.action = action
            context.startService(intent)
        }
    }

}
