package com.developers.sprintsync.core.util.permission

import android.Manifest
import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.Fragment

class LocationPermissionManager(
    caller: ActivityResultCaller,
    private val context: Context,
    private val shouldShowPermissionRational: (permission: String) -> Boolean,
) {

    /*
    constructor(activity: ComponentActivity) : this(
        caller = activity as ActivityResultCaller,
        context = activity,
        shouldShowPermissionRational = { activity.shouldShowRequestPermissionRationale(it) },
    )
     */

    constructor(fragment: Fragment) : this(
        caller = fragment,
        context = fragment.requireContext(),
        shouldShowPermissionRational = { fragment.shouldShowRequestPermissionRationale(it) },
    )

    private val permissionManager =
        PermissionManager(caller, context, shouldShowPermissionRational)

    fun requestPermission(
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null,
        onPermissionRational: (() -> Unit)? = null,
    ) = permissionManager.requestPermissions(
        PERMISSION,
        onPermissionsGranted = onPermissionsGranted,
        onPermissionRational = onPermissionRational,
    )

    fun shouldShowPermissionRationale(): Boolean = shouldShowPermissionRational.invoke(PERMISSION)

    companion object {
        const val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

        fun hasPermission(context: Context): Boolean = PermissionManager.hasPermission(context, PERMISSION)
    }
}

class MissingLocationPermissionException() : Exception()