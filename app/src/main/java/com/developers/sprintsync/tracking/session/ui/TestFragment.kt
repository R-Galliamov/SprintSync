package com.developers.sprintsync.tracking.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.global.util.extension.findTopNavController

class TestFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { "binding is null" }

    private val sessionViewModel by activityViewModels<TestViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setFlowObserver()
        setBackButtonListener()
    }

    private fun setBackButtonListener() {
        binding.btBack.setOnClickListener {
            findTopNavController().navigateUp()
        }
    }

    private fun setFlowObserver() {
        sessionViewModel.flow.observe(viewLifecycleOwner) { value ->
            binding.tvDuration.text = value.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val tag = "My stack"
}
