package com.developers.sprintsync.util.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.sprintsync.R

fun AppCompatActivity.getRootNavController(): NavController {
    val navHost =
        supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
    return navHost.navController
}
