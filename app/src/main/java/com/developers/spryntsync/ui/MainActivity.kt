package com.developers.spryntsync.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.developers.spryntsync.databinding.ActivityMainBinding
import com.developers.spryntsync.util.NavigationManager
import com.developers.spryntsync.util.extension.getRootNavController

const val TAG = "My log"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //installSplashScreen() //todo set splash screen
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigation()

    }

    private fun setNavigation() {
        val isFirstRun = true // todo replace
        val navController = getRootNavController()
        NavigationManager.prepareRootNavController(isFirstRun, navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}