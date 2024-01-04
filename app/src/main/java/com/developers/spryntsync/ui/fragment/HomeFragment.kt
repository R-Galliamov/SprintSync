package com.developers.spryntsync.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.spryntsync.R
import com.developers.spryntsync.databinding.FragmentHomeBinding
import com.developers.spryntsync.util.extension.findTopNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
        findTopNavController().navigate(R.id.onboardingFragment) // todo: remove this code as far it's just an example
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}