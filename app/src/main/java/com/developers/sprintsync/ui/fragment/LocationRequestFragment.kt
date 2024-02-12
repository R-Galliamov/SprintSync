package com.developers.sprintsync.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentLocationRequestBinding
import com.developers.sprintsync.manager.permission.LocationPermissionManager

class LocationRequestFragment : Fragment() {

    private var _binding: FragmentLocationRequestBinding? = null
    private val binding: FragmentLocationRequestBinding
        get() = _binding!!

    private var permissionManager: LocationPermissionManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPermissionManager()

        if (permissionManager?.shouldShowPermissionRational() == true) {
            binding.tvAllowPermission.text = "To settings"
        } else {
            binding.tvAllowPermission.text = "Allow"
        }

        binding.btAllowPermission.setOnClickListener {
            permissionManager?.requestPermission(
                onPermissionsGranted = { isGranted ->
                    if (isGranted) {
                        findNavController().navigate(R.id.action_locationRequestFragment_to_runDash)
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionManager = null
        _binding = null
    }

    private fun initPermissionManager() {
        permissionManager = LocationPermissionManager(this)
    }
}