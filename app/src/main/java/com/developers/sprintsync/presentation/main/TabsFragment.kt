package com.developers.sprintsync.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.core.navigation.PermissionNavigator
import com.developers.sprintsync.core.util.extension.showError
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentTabsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment managing the main navigation tabs and FAB for starting a workout.
 */
@AndroidEntryPoint
class TabsFragment : Fragment() {
    private var _binding: FragmentTabsBinding? = null
    private val binding get() = checkNotNull(_binding) { R.string.binding_init_error }

    @Inject
    lateinit var log: AppLogger

    @Inject
    lateinit var permissionNavigator: PermissionNavigator

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
        try {
            setNavMenu()
            setListeners()
            log.i("TabsFragment view created")
        } catch (e: Exception) {
            log.e("Failed to initialize view: ${e.message}", e)
            showError(log)
        }
    }

    // Sets up bottom navigation menu
    private fun setNavMenu() {
        try {
            val navHost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
            val navController = navHost.navController

            binding.bottomNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        navController.navigate(R.id.nav_graph_home)
                        log.i("Navigated to Home tab")
                        true
                    }

                    R.id.history -> {
                        navController.navigate(R.id.nav_graph_history)
                        log.i("Navigated to History tab")
                        true
                    }

                    R.id.statistics -> {
                        navController.navigate(R.id.nav_graph_statistics)
                        log.i("Navigated to Statistics tab")
                        true
                    }

                    R.id.parameters -> {
                        navController.navigate(R.id.nav_graph_parameters)
                        log.i("Navigated to Parameters tab")
                        true
                    }

                    else -> {
                        log.e("Unknown navigation item: ${item.itemId}")
                        false
                    }
                }
            }
        } catch (e: Exception) {
            log.e("Failed to set up navigation menu: ${e.message}", e)
            showError(log, null)
        }
    }

    // Sets up FAB click listener
    private fun setListeners() {
        binding.fabRun.setOnClickListener {
            try {
                permissionNavigator.routeUser(findNavController())
            } catch (e: Exception) {
                log.e("FAB click error: ${e.message}", e)
                showError(log, null)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        log.d("${this.javaClass.simpleName} view destroyed")
    }
}
