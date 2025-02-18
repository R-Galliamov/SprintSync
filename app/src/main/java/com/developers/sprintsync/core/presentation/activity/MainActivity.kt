package com.developers.sprintsync.core.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.ActivityMainBinding
import com.developers.sprintsync.core.navigation.NavigationManager
import com.developers.sprintsync.core.util.extension.getRootNavController
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "My stack"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // installSplashScreen() // TODO set splash screen
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigation()
    }

    private fun setNavigation() {
        val isFirstRun = true // TODO replace
        val navController = getRootNavController()
        NavigationManager.prepareRootNavController(isFirstRun, navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
