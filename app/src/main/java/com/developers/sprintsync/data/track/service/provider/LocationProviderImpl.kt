package com.developers.sprintsync.data.track.service.provider

import android.Manifest
import android.content.Context
import android.os.Looper
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.permission.MissingPermissionException
import com.developers.sprintsync.core.util.permission.PermissionManager
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.toDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Interface for providing location updates
interface LocationProvider {
    val isRunning: Boolean

    val locationFlow: Flow<LocationModel>

    fun start()

    fun stop()
}

// Implementation of LocationProvider using Google Fused Location API
class LocationProviderImpl
@Inject
constructor(
    private val context: Context,
    private val scope: CoroutineScope,
    private val log: AppLogger,
) : LocationProvider {
    private val _locationFlow = MutableSharedFlow<LocationModel>(replay = 0)
    override val locationFlow = _locationFlow.asSharedFlow()

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            context,
        )
    }

    private val locationRequest: LocationRequest =
        LocationRequest
            .Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_UPDATE_INTERVAL,
            ).setMinUpdateDistanceMeters(LOCATION_UPDATE_MIN_DISTANCE)
            .build()

    private var locationCallback: LocationCallback? = null

    private var _isRunning: Boolean = false
    override val isRunning get() = _isRunning

    /**
     * Starts location updates using the Fused Location Provider.
     * @throws MissingPermissionException if the app lacks location permissions.
     * @throws SecurityException if the location provider encounters a security issue.
     * @throws Exception for other unexpected errors during location update initialization.
     */
    override fun start() {
        if (_isRunning) {
            log.i("LocationProvider already running, skipping start")
            return
        }

        if (PermissionManager.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION).not()) {
            log.e("Missing location permission")
            throw MissingPermissionException()
        }

        locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.lastLocation?.let { location ->
                        scope.launch {
                            val locationModel = location.toDataModel()
                            _locationFlow.emit(locationModel)
                            log.d("New location emitted: $locationModel")
                        }
                    }
                }
            }

        try {
            client.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper(),
            )

            _isRunning = true
            log.i("Location updates started")
        } catch (e: SecurityException) {
            log.e("SecurityException starting location updates: ${e.message}", e)
            throw e
        } catch (e: Exception) {
            log.e("Failed to start location updates: ${e.message}", e)
            throw e
        }
    }

    // Stops location updates
    override fun stop() {
        if (!_isRunning) {
            log.i("LocationProvider not running, skipping stop")
            return
        }

        locationCallback?.let { callback ->
            client.removeLocationUpdates(callback)
            log.i("Location updates stopped")
        }
        locationCallback = null
        _isRunning = false
    }

    private companion object {
        const val LOCATION_UPDATE_INTERVAL = 1000L
        const val LOCATION_UPDATE_MIN_DISTANCE = 0F
    }
}
