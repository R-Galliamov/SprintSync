package com.developers.spryntsync.util.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.spryntsync.R

fun AppCompatActivity.getRootNavController() : NavController {
    val navHost = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
    return navHost.navController
}