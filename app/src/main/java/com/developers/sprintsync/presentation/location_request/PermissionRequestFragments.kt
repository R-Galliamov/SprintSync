package com.developers.sprintsync.presentation.location_request

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import com.developers.sprintsync.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for requesting location permission from the user.
 * Child of BasePermissionFragment; provides config for location.
 */
@AndroidEntryPoint
class LocationRequestFragment : BasePermissionFragment() {
    override val screenConfig: PermissionScreenConfig = PermissionScreenConfig(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        iconResId = R.drawable.im_location_request,
        titleResId = R.string.permission_location_title,
        descriptionResId = R.string.permission_location_description,
    )
}

/**
 * Fragment for requesting notification permission (Android 13+).
 * Child of BasePermissionFragment; provides config for notifications.
 */
@AndroidEntryPoint
class NotificationRequestFragment : BasePermissionFragment() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override val screenConfig: PermissionScreenConfig = PermissionScreenConfig(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        iconResId = R.drawable.im_notification_request,
        titleResId = R.string.permission_notifications_title,
        descriptionResId = R.string.permission_notifications_description,
    )
}

