package com.developers.sprintsync.global.manager.navigation

import androidx.navigation.NavController

object NavigationManager {
    private val navGraphTopManager = NavGraphTopManager()

    fun prepareRootNavController(
        isFirstRun: Boolean,
        rootNavController: NavController,
    ) {
        val graph = rootNavController.navInflater.inflate(navGraphTopManager.getNavGraphId())
        graph.setStartDestination(getStartDestination(isFirstRun))
        rootNavController.graph = graph
    }

    private fun getStartDestination(isFirstRun: Boolean): Int =
        if (isFirstRun) {
            navGraphTopManager.getOnboardingDestination()
        } else {
            navGraphTopManager.getTabsDestination()
        }
}
