package com.developers.sprintsync.core.util.extension

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.log.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Retrieves the top-level NavController for navigation.
 * @return The [NavController] from the top-level NavHostFragment or the default Fragment NavController.
 */
fun Fragment.findTopNavController(): NavController {
    val topLevelHost =
        requireActivity().supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment?
    return topLevelHost?.navController ?: findNavController()
}

/**
 * Observes a Flow and collects its values in a lifecycle-aware manner.
 * @param flow The [Flow] to observe.
 * @param collector The suspend function to handle collected values.
 */
fun <T> Fragment.observe(
    flow: Flow<T>,
    collector: suspend (T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { collector(it) }
        }
    }
}

/**
 * Displays an error toast if the Fragment is added and has a valid view.
 * @param log The [AppLogger] for logging the error.
 * @param message Optional error message; defaults to a generic unexpected error string.
 */
fun Fragment.showError(log: AppLogger, message: String? = null ) {
    if (this.isAdded && this.view != null) {
        val context = this.context ?: return
        val errMessage = message ?: context.getString(R.string.error_unexpected)
        showToast(errMessage)
        log.e("Error displayed: $errMessage", Exception(errMessage))
    }
}

/**
 * Displays an error toast and navigates back if the Fragment is added and has a valid view.
 * @param log The [AppLogger] for logging the error.
 * @param message Optional error message; defaults to a generic unexpected error string.
 */
fun Fragment.showErrorAndBack(log: AppLogger, message: String? = null, ) {
    if (this.isAdded && this.view != null) {
        showError(log, message)
        navigateBack(log)
    }
}

// Navigates back using the NavController
fun Fragment.navigateBack(log: AppLogger) {
    try {
        if (this.isAdded) {
            this.findNavController().popBackStack()
            log.i("Navigated back")
        }
    } catch (e: Exception) {
        log.e( "Error navigating back",e)
    }
}

// Shows a short-duration toast message
fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}