package com.developers.sprintsync.tracking.util.manager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// TODO test on physical device
class MarkerManager(
    private val map: GoogleMap,
    private val context: Context,
) : SensorEventListener {
    private var marker: Marker? = null
    private var currentHeading: Float = 0f
    private var sensorManager: SensorManager? = null

    init {
        sensorManager = context.getSystemService(SensorManager::class.java)
        registerSensors()
    }

    private fun registerSensors() {
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val rotationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(rotationMatrix, null, event.values, event.values)
        Log.d("My stack", "onSensorChanged: $success")
        if (success) {
            val orientationValues = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationValues)
            // Get device heading in radians
            currentHeading = -orientationValues[0] // Convert to degrees by multiplying by (180 / PI)
            updateMarkerRotation()
        }
    }

    private fun updateMarkerRotation() {
        marker?.rotation = Math.toDegrees(currentHeading.toDouble()).toFloat()
    }

    fun updateMarkerLocation(location: LatLng) {
        // Remove existing marker
        marker?.remove()

        // Add marker for new location
        marker =
            map.addMarker(
                MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Current Location"),
            )
        Log.d("My stack", "updateMarkerLocation: ${marker?.rotation}")
        // Update marker rotation
        updateMarkerRotation()
    }

    fun cleanup() {
        // Unregister sensor listeners
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
        // NO - OP
    }
}
