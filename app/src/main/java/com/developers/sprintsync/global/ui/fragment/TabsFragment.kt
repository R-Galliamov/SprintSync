package com.developers.sprintsync.global.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTabsBinding
import com.developers.sprintsync.global.manager.permission.LocationPermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabsFragment : Fragment() {
    private var _binding: FragmentTabsBinding? = null
    private val binding get() = checkNotNull(_binding) { R.string.binding_init_error }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setNavMenu()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNavMenu() {
        val navHost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.nav_graph_home)
                    true
                }

                R.id.history -> {
                    navController.navigate(R.id.nav_graph_history)
                    true
                }

                R.id.statistics -> {
                    navController.navigate(R.id.nav_graph_statistics)
                    true
                }

                R.id.parameters -> {
                    navController.navigate(R.id.nav_graph_parameters)
                    true
                }
                else -> false
            }
        }
    }

    private fun setListeners() {
        binding.fabRun.setOnClickListener {
            if (LocationPermissionManager.hasPermission(requireContext())) {
                findNavController().navigate(R.id.action_tabsFragment_to_trackingFragment)
            } else {
                findNavController().navigate(R.id.action_tabsFragment_to_locationRequestFragment)
            }
        }
    }
}
