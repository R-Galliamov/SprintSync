package com.developers.spryntsync.util

import androidx.navigation.NavController

object NavigationManager {

    private val navGraphMainManager = NavGraphMainManager()

    fun prepareRootNavController(isFirstRun: Boolean, rootNavController: NavController) {
        val graph = rootNavController.navInflater.inflate(navGraphMainManager.getNavGraphId())
        graph.setStartDestination(getStartDestination(isFirstRun))
        rootNavController.graph = graph
    }

    private fun getStartDestination(isFirstRun: Boolean): Int = if (isFirstRun) {
        navGraphMainManager.getOnboardingDestination()
    } else {
        navGraphMainManager.getTabsDestination()
    }
}
