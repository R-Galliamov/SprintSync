package com.developers.sprintsync.tracking.data.provider.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.developers.sprintsync.permission.domain.LocationPermissionManager
import com.developers.sprintsync.core.util.error.NoPermissionsException
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.toDataModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProviderImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LocationProvider {
        private companion object {
            const val LOCATION_UPDATE_INTERVAL = 5000L
            const val LOCATION_UPDATE_MIN_DISTANCE = 0F
        }

        private val client: FusedLocationProviderClient by lazy {
            LocationServices.getFusedLocationProviderClient(
                context,
            )
        }

        @SuppressLint("MissingPermission")
        override fun locationFlow(): Flow<LocationModel> {
            val request =
                LocationRequest
                    .Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        LOCATION_UPDATE_INTERVAL,
                    ).setMinUpdateDistanceMeters(LOCATION_UPDATE_MIN_DISTANCE)
                    .build()

            return callbackFlow {
                if (!hasLocationPermission()) throw NoPermissionsException
                val locationCallback =
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            super.onLocationResult(result)
                            result.lastLocation?.let { launch { send(it.toDataModel()) } }
                        }
                    }

                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }
        }

        override fun hasLocationPermission(): Boolean = LocationPermissionManager.hasPermission(context)
    }
