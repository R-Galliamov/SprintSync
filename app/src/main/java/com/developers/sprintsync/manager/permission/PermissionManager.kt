package com.developers.sprintsync.manager.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

interface PermissionManager {

    companion object {
        fun checkPermission(context: Context, permission: String): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED

        fun checkLocationPermission(context: Context): Boolean =
            checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun checkPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)?
    )
}