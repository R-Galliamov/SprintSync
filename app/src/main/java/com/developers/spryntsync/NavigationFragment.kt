package com.developers.spryntsync

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.spryntsync.databinding.FragmentNavigationBinding

const val isFirstRun = false //TODO replace with SharedPref


class NavigationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "NavigationFragment")
        return FragmentNavigationBinding.inflate(inflater, container, false).root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        if (isFirstRun) {
            findNavController().navigate(R.id.action_navigationFragment_to_onboardingFragment)
        } else {
            findNavController().navigate(R.id.action_navigationFragment_to_mainFragment)
        }
    }
}