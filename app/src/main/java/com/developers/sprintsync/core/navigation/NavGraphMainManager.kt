package com.developers.sprintsync.core.navigation

import com.developers.sprintsync.R

/*
 * Manages main navigation graph .
 */
class NavGraphMainManager {
    fun getNavGraphId(): Int = R.navigation.nav_graph_main

    fun getOnboardingDestination(): Int = R.id.onboardingFragment

    fun getTabsDestination(): Int = R.id.tabsFragment
}
