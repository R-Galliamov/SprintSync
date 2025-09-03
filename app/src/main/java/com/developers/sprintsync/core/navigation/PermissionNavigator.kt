package com.developers.sprintsync.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.PermissionChecker
import javax.inject.Inject

/*
 * Handles navigation based on permission status.
 */
class PermissionNavigator @Inject constructor(
    private val permissionChecker: PermissionChecker,
    private val log: AppLogger,
) {

    /*
     * Navigates to the next destination based on permission status.
     */
    fun routeUser(navController: NavController) {
        val currentId =
            navController.currentDestination?.id ?: throw IllegalStateException("No current destination found")
        val nextId = determineNextDestination()

        val inclusive = (currentId == R.id.locationRequestFragment || currentId == R.id.notificationRequestFragment)
        val navOptions = NavOptions.Builder().setPopUpTo(currentId, inclusive).build()
        navController.navigate(nextId, null, navOptions)
        log.i("Navigated to next destination: $nextId")
    }

    private fun determineNextDestination(): Int = if (!permissionChecker.hasLocation()) {
        R.id.locationRequestFragment
    } else if (!permissionChecker.hasNotification()) {
        R.id.notificationRequestFragment
    } else {
        R.id.trackingFragment
    }
}