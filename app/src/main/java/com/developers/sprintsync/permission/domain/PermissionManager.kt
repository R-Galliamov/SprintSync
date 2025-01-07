package com.developers.sprintsync.permission.domain

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class PermissionManager(
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

    private var onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionsGranted?.invoke(isGranted)
        }

    private val requestMultiplePermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val isGranted = result.values.all { it }
            onPermissionsGranted?.invoke(isGranted)
        }

    private fun requestPermissions(permissionsToBeRequested: List<String>) {
        if (permissionsToBeRequested.size > 1) {
            requestMultiplePermissionLauncher.launch(permissionsToBeRequested.toTypedArray())
        } else {
            requestPermissionLauncher.launch(permissionsToBeRequested.firstOrNull())
        }
    }

    fun requestPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null,
        onPermissionRational: (() -> Unit)? = null,
    ) {
        this.onPermissionsGranted = onPermissionsGranted

        val permissionsToBeRequested =
            permissions.filter { permission -> !hasPermission(context, permission) }
        val shouldShowPermissionRational =
            permissionsToBeRequested.any {
                shouldShowPermissionRational.invoke(it)
            }
        when {
            permissionsToBeRequested.isEmpty() -> {
                onPermissionsGranted?.invoke(true)
            }

            shouldShowPermissionRational -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                onPermissionRational?.invoke()
                onPermissionsGranted?.invoke(false)
            }

            else -> requestPermissions(permissionsToBeRequested)
        }
    }

    companion object {
        fun hasPermission(
            context: Context,
            permission: String,
        ): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                permission,
            ) == PackageManager.PERMISSION_GRANTED
    }
}
