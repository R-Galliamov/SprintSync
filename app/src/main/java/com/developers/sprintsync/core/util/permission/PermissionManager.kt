package com.developers.sprintsync.core.util.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.developers.sprintsync.core.util.log.AppLogger
import javax.inject.Inject

/**
 * Checks for location and notification permissions.
 */
class PermissionChecker @Inject constructor(private val context: Context) {

    fun hasLocation() =
        PermissionManager.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)


    fun hasNotification() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionManager.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
    } else true
}

/**
 * Exception thrown when a required permission is missing.
 */
class MissingPermissionException(
    message: String = "Required permission is missing",
) : Exception(message)

/**
 * Manages Android permission checks and requests.
 */
class PermissionManager(
    caller: ActivityResultCaller,
    private val context: Context,
    private val permission: String,
    private val shouldShowRationale: (permission: String) -> Boolean,
    private val log: AppLogger,
) {

    constructor(fragment: Fragment, permission: String, log: AppLogger) : this(
        caller = fragment,
        context = fragment.requireContext(),
        permission = permission,
        shouldShowRationale = { fragment.shouldShowRequestPermissionRationale(it) },
        log = log,
    )

    private var onResult: ((isGranted: Boolean) -> Unit)? = null

    private val requestLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            try {
                log.i("Permission request result: $permission granted=$isGranted")
                onResult?.invoke(isGranted)
            } catch (e: Exception) {
                log.e("Error handling permission result: ${e.message}", e)
            }
        }

    /**
     * Checks if the permission rationale should be shown.
     * @return True if the rationale should be displayed.
     */
    fun shouldShowRationale() = shouldShowRationale.invoke(permission)

    /**
     * Checks if the permission has been granted.
     * @return True if the permission is granted.
     */
    fun hasPermission() = hasPermission(context, permission)

    /**
     * Requests the permission, handling rationale and result callbacks.
     * @param onResult Callback for permission result.
     * @param onRationale Callback to show rationale if needed.
     */
    fun requestPermission(
        onResult: ((isGranted: Boolean) -> Unit)? = null,
        onRationale: (() -> Unit)? = null,
    ) {
        require(permission.isNotEmpty()) { "Permission string cannot be empty" }
        this.onResult = onResult

        when {
            hasPermission() -> {
                log.i("Permission already granted: $permission")
                onResult?.invoke(true)
            }

            shouldShowRationale() -> {
                log.i("Showing permission rationale: $permission")
                onRationale?.invoke()
            }

            else -> requestSystemPermission()
        }
    }

    // Launches the system permission request
    private fun requestSystemPermission() {
        try {
            requestLauncher.launch(permission)
            log.d("Launching permission request: $permission")
        } catch (e: Exception) {
            log.e("Error launching permission request: ${e.message}", e)
            onResult?.invoke(false)
        }
    }

    companion object {
        fun hasPermission(context: Context, permission: String): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                permission,
            ) == PackageManager.PERMISSION_GRANTED
    }
}
