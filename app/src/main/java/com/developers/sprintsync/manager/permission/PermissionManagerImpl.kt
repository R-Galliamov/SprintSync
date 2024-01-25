package com.developers.sprintsync.manager.permission

import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.developers.sprintsync.manager.permission.PermissionManager.Companion.checkPermission
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(
    caller: ActivityResultCaller,
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val shouldShowPermissionRational: (permission: String) -> Boolean
) : PermissionManager {

    constructor(activity: ComponentActivity) : this(
        caller = activity as ActivityResultCaller,
        context = activity,
        fragmentManager = (activity as AppCompatActivity).supportFragmentManager,
        shouldShowPermissionRational = { activity.shouldShowRequestPermissionRationale(it) }
    )

    constructor(fragment: Fragment) : this(
        caller = fragment,
        context = fragment.requireContext(),
        fragmentManager = fragment.parentFragmentManager,
        shouldShowPermissionRational = { fragment.shouldShowRequestPermissionRationale(it) }
    )

    private var onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private val requestMultiplePermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val isGranted = result.values.all { it == true }
            if (!isGranted) {
                // As above: Handle the case where permissions are denied.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private fun requestPermissions(permissionsToBeRequested: List<String>) {
        if (permissionsToBeRequested.size > 1) {
            requestMultiplePermissionLauncher.launch(permissionsToBeRequested.toTypedArray())
        } else {
            requestPermissionLauncher.launch(permissionsToBeRequested.firstOrNull())
        }
    }

    override fun checkPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)?
    ) {
        this.onPermissionsGranted = onPermissionsGranted

        val permissionsToBeRequested =
            permissions.filter { permission -> !checkPermission(context, permission) }
        val shouldShowPermissionRational = permissionsToBeRequested.any {
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
                onPermissionsGranted?.invoke(false)
            }

            else -> requestPermissions(permissionsToBeRequested)
        }
    }
}