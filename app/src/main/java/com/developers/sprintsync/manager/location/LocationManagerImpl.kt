package com.developers.sprintsync.manager.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.developers.sprintsync.error.NoPermissionsException
import com.developers.sprintsync.manager.permission.LocationPermissionManager
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
class LocationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationManager {

    companion object {
        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2F
    }

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            context
        )
    }

    @SuppressLint("MissingPermission")
    override fun listenToLocation(): Flow<Location> {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL
        ).setMinUpdateDistanceMeters(FASTEST_LOCATION_INTERVAL).build()

        return callbackFlow {
            if (!hasLocationPermission()) throw NoPermissionsException
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    Log.i(
                        "My Stack",
                        "Lat is: ${result.lastLocation?.latitude}, " +
                                "Long is: ${result.lastLocation?.longitude}"
                    )
                    result.lastLocation?.let { launch { send(it) } }
                }
            }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose { client.removeLocationUpdates(locationCallback) }
        }
    }

    override fun hasLocationPermission(): Boolean = LocationPermissionManager.hasPermission(context)
}
