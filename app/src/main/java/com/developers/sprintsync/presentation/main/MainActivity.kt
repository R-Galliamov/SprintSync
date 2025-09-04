package com.developers.sprintsync.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.ActivityMainBinding
import com.developers.sprintsync.core.navigation.NavigationManager
import com.developers.sprintsync.core.util.extension.getRootNavController
import com.developers.sprintsync.core.util.log.AppLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TAG = "My stack"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

    @Inject
    lateinit var log: AppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigation()
    }

    private fun setNavigation() {
        val navController = getRootNavController()
        NavigationManager.prepareRootNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

