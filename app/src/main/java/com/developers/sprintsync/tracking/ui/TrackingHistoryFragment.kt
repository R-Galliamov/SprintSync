package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingHistoryBinding

class TrackingHistoryFragment : Fragment() {
    private var _binding: FragmentTrackingHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
