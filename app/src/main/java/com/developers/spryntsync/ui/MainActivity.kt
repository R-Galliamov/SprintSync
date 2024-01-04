package com.developers.spryntsync.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.spryntsync.R
import com.developers.spryntsync.databinding.ActivityMainBinding

const val TAG = "My log"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //installSplashScreen()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = getRootNavController()
        prepareRootNavController(false, navController)
    }

    private fun prepareRootNavController(isFirstRun: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if (!isFirstRun) {
                getTabsDestination()
            } else {
                getOnboardingDestination()
            }
        )
        navController.graph = graph
    }

    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        return navHost.navController
    }


    fun getMainNavigationGraphId(): Int = R.navigation.nav_graph_main

    fun getOnboardingDestination(): Int = R.id.onboardingFragment

    fun getTabsDestination(): Int = R.id.tabsFragment


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}