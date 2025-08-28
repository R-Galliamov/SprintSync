package com.developers.sprintsync.presentation.location_request

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
import com.developers.sprintsync.core.navigation.PermissionNavigator
import com.developers.sprintsync.core.util.extension.navigateBack
import com.developers.sprintsync.core.util.extension.showError
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.permission.PermissionManager
import com.developers.sprintsync.databinding.FragmentPermissionRequestBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * UI and logic configuration for a permission request screen.
 */
data class PermissionScreenConfig(
    val permission: String,
    val iconResId: Int,
    val titleResId: Int,
    val descriptionResId: Int,
)

/**
 * Abstract base Fragment for handling permission requests in the app.
 * Manages rationale display, permission workflow, navigation, and error logging.
 * Child fragments must provide PermissionScreenConfig for UI and logic.
 */
@AndroidEntryPoint
abstract class BasePermissionFragment : Fragment() {
    private var _binding: FragmentPermissionRequestBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

    /** Defines screen UI and permission logic for the inheritor. */
    abstract val screenConfig: PermissionScreenConfig

    @Inject
    lateinit var log: AppLogger

    @Inject
    lateinit var permissionNavigator: PermissionNavigator

    private val permissionManager: PermissionManager by lazy {
        PermissionManager(
            this,
            screenConfig.permission,
            log,
        )
    }

    /**
     * Navigates to the next app screen using.
     * Handles navigation errors with user feedback.
     */
    private fun navigateToNextScreen() {
        try {
            permissionNavigator.routeUser(findNavController())
        } catch (e: Exception) {
            log.e("Error navigating to next screen", e)
            showError(log)
        }
    }

    // Lifecycle: check permission on each start; auto-advance if already granted.
    override fun onStart() {
        super.onStart()
        try {
            if (permissionManager.hasPermission()) {
                navigateToNextScreen()
                log.i("${screenConfig.permission} permission already granted, navigating further")
            }
        } catch (e: Exception) {
            log.e("Error checking permission on start", e)
            showErrorAndBack(log)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPermissionRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        try {
            setScreenConfiguration()
            updateUi(permissionManager.shouldShowRationale())
            setupBackButton()
            log.d("LocationRequestFragment view created")
        } catch (e: Exception) {
            log.e("Error setting up view", e)
            showErrorAndBack(log)
        }
    }

    /** Set icon, title, description for the permission request screen. */
    private fun setScreenConfiguration() {
        binding.ivPermission.setImageResource(screenConfig.iconResId)
        binding.tvTitle.text = getString(screenConfig.titleResId)
        binding.tvDescription.text = getString(screenConfig.descriptionResId)
    }

    /**
     * Update button and click handler according to rationale state.
     * If rationale required, show 'Go to Settings'; otherwise, show 'Allow'.
     */
    private fun updateUi(shouldShowRational: Boolean) {
        updateButtonText(shouldShowRational)
        updateButtonListener(shouldShowRational)
    }

    /** Sets up confirm button logic for requesting permission or opening settings. */
    private fun updateButtonListener(shouldShowRational: Boolean) {
        binding.btnConfirm.setOnClickListener {
            try {
                if (shouldShowRational) {
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

    /** Sets button text according to whether rationale is shown. */
    private fun updateButtonText(shouldShowRationale: Boolean) {
        binding.tvAllowPermission.text = if (shouldShowRationale) {
            getString(R.string.action_open_settings)
        } else {
            getString(R.string.action_allow)
        }
        log.d("Button text updated: showRationale=$shouldShowRationale")
    }

    /**
     * Called after the user grants or denies permission.
     * If granted — navigate forward, if denied — update UI (show rationale if needed).
     */
    private fun onPermissionResult(isGranted: Boolean) {
        try {
            if (isGranted) {
                navigateToNextScreen()
                log.i("Permission granted, navigating to next screen")
            } else {
                updateUi(permissionManager.shouldShowRationale())
                log.i("Permission denied")
            }
        } catch (e: Exception) {
            log.e("Error handling permission result", e)
            showError(log)
        }
    }

    /**
     * Launches app settings so user can grant permission manually.
     * Triggered if permission cannot be requested normally.
     */
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
