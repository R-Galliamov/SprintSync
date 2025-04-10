package com.developers.sprintsync.domain.tracking_service.internal.provider

import android.content.Context
import android.os.Looper
import android.util.Log
import com.developers.sprintsync.core.permission.LocationPermissionManager
import com.developers.sprintsync.core.util.error.NoPermissionsException
import com.developers.sprintsync.domain.tracking_service.model.LocationModel
import com.developers.sprintsync.domain.tracking_service.model.toDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LocationProvider {
    val isRunning: Boolean

    val locationFlow: Flow<LocationModel>

    fun start()

    fun stop()
}

class DefaultLocationProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val scope: CoroutineScope,
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

        override fun start() {
            if (_isRunning) return
            if (LocationPermissionManager.hasPermission(context).not()) throw NoPermissionsException

            locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.lastLocation?.let { location ->
                            scope.launch {
                                _locationFlow.emit(location.toDataModel())
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
            } catch (e: SecurityException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start location updates: ${e.message}", e)
                throw e
            }
        }

        override fun stop() {
            if (!_isRunning) return

            locationCallback?.let { callback ->
                client.removeLocationUpdates(callback)
            }
            locationCallback = null
            _isRunning = false
        }

        private companion object {
            const val LOCATION_UPDATE_INTERVAL = 5000L
            const val LOCATION_UPDATE_MIN_DISTANCE = 0F

            const val TAG = "Location Provider"
        }
    }
