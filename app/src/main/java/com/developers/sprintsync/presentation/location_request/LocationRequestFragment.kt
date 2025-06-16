package com.developers.sprintsync.presentation.location_request

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.navigateBack
import com.developers.sprintsync.core.util.extension.showError
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.permission.PermissionManager
import com.developers.sprintsync.databinding.FragmentLocationRequestBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for requesting location permissions and guiding the user to enable them.
 */
@AndroidEntryPoint
class LocationRequestFragment : Fragment() {
    private var _binding: FragmentLocationRequestBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    @Inject
    lateinit var log: AppLogger

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            log,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLocationRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        try {
            if (permissionManager.hasPermission()) {
                navigateToTracking()
                log.i("Location permission already granted, navigating to tracking")
            }
        } catch (e: Exception) {
            log.e("Error checking permission on start", e)
            showErrorAndBack(log)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        try {
            updateUi(permissionManager.shouldShowRationale())
            setupBackButton()
            log.d("LocationRequestFragment view created")
        } catch (e: Exception) {
            log.e("Error setting up view", e)
            showErrorAndBack(log)
        }
    }

    // Handles permission result
    private fun onPermissionResult(isGranted: Boolean) {
        try {
            if (isGranted) {
                navigateToTracking()
                log.i("Permission granted, navigating to tracking")
            } else {
                updateUi(permissionManager.shouldShowRationale())
                log.i("Permission denied")
            }
        } catch (e: Exception) {
            log.e("Error handling permission result", e)
            showError(log)
        }
    }

    // Updates UI based on permission rationale state
    private fun updateUi(shouldShowPermissionRational: Boolean) {
        updateButtonText(shouldShowPermissionRational)
        updateButtonListener(shouldShowPermissionRational)
    }

    private fun updateButtonListener(shouldShowPermissionRational: Boolean) {
        binding.btnConfirm.setOnClickListener {
            try {
                if (shouldShowPermissionRational) {
                    openSettings()
                } else {
                    log.i("Requesting location permission")
                    permissionManager.requestPermission(
                        onResult = { isGranted ->
                            onPermissionResult(isGranted)
                        },
                    )
                }
            } catch (e: Exception) {
                log.e("Error handling button click", e)
                showError(log)
            }
        }
    }

    // Updates button text based on permission rationale
    private fun updateButtonText(shouldShowRationale: Boolean) {
        binding.tvAllowPermission.text = if (shouldShowRationale) {
            getString(R.string.to_settings)
        } else {
            getString(R.string.allow)
        }
        log.d("Button text updated: showRationale=$shouldShowRationale")
    }

    private fun navigateToTracking() {
        try {
            findNavController().navigate(R.id.action_locationRequestFragment_to_trackingFragment)
            log.i("Navigated to tracking screen")
        } catch (e: Exception) {
            log.e("Error navigating to tracking screen", e)
            showError(log)
        }
    }

    private fun openSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts(PACKAGE_SCHEME, requireActivity().packageName, null)
            }
            startActivity(intent)
            log.i("Opened app settings")
        } catch (e: Exception) {
            log.e("Error opening app settings", e)
            showError(log)
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            navigateBack(log)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        log.d("Fragment view destroyed")
    }

    companion object {
        private const val PACKAGE_SCHEME = "package"
    }
}
