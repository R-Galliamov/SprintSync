package com.developers.sprintsync.global.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentLocationRequestBinding
import com.developers.sprintsync.global.manager.permission.LocationPermissionManager

class LocationRequestFragment : Fragment() {
    private var _binding: FragmentLocationRequestBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val permissionManager: LocationPermissionManager by lazy {
        LocationPermissionManager(
            this,
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
        if (LocationPermissionManager.hasPermission(requireContext())) {
            navigateToTrackingScreen()
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        updateUiBasedOnPermissionState(permissionManager.shouldShowPermissionRational())
        setBackButtonListener()
    }

    private fun onPermissionGranted(isGranted: Boolean) {
        when (isGranted) {
            true -> navigateToTrackingScreen()
            false -> {
                updateUiBasedOnPermissionState(permissionManager.shouldShowPermissionRational())
            }
        }
    }

    private fun updateUiBasedOnPermissionState(shouldShowPermissionRational: Boolean) {
        updateButtonText(shouldShowPermissionRational)
        updateButtonListener(shouldShowPermissionRational)
    }

    private fun updateButtonListener(shouldShowPermissionRational: Boolean) {
        when (shouldShowPermissionRational) {
            true ->
                binding.btConfirmPermission.setOnClickListener {
                    openAppSettings()
                }

            false -> {
                binding.btConfirmPermission.setOnClickListener {
                    permissionManager.requestPermission(
                        onPermissionsGranted = { isGranted ->
                            onPermissionGranted(isGranted)
                        },
                    )
                }
            }
        }
    }

    private fun updateButtonText(shouldShowPermissionRational: Boolean) {
        when (shouldShowPermissionRational) {
            true -> binding.tvAllowPermission.text = getString(R.string.to_settings)
            false -> binding.tvAllowPermission.text = getString(R.string.allow)
        }
    }

    private fun navigateToTrackingScreen() {
        findNavController().navigate(R.id.action_locationRequestFragment_to_trackingFragment)
    }

    private fun openAppSettings() {
        Log.d(TAG, "openAppSettings")
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri =
            Uri.fromParts(PACKAGE_SCHEME, requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun setBackButtonListener() {
        binding.btBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PACKAGE_SCHEME = "package"
        private const val TAG = "My Stack: LocationRequestFragment"
    }
}
