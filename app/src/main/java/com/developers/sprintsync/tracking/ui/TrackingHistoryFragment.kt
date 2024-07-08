package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingHistoryBinding
import com.developers.sprintsync.global.ui.fragment.TabsFragmentDirections
import com.developers.sprintsync.global.util.extension.findTopNavController
import com.developers.sprintsync.tracking.adapter.TrackingHistoryAdapter
import com.developers.sprintsync.tracking.viewModel.TrackingHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingHistoryFragment : Fragment() {
    private var _binding: FragmentTrackingHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<TrackingHistoryViewModel>()

    private val onInteractionListener =
        object : TrackingHistoryAdapter.OnInteractionListener {
            override fun onItemSelected(trackId: Int) {
                navigateToTrackHistoryStatistics(trackId)
            }
        }

    private val adapter by lazy { TrackingHistoryAdapter(onInteractionListener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerViewAdapter()
        setTracksObserver()
    }

    private fun setTracksObserver() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                adapter.submitList(tracks)
            } else {
                // TODO show empty view
            }
        }
    }

    private fun setRecyclerViewAdapter() {
        binding.rvTrackingHistory.adapter = adapter
    }

    private fun navigateToTrackHistoryStatistics(trackId: Int) {
        val action =
            TabsFragmentDirections.actionTabsFragmentToTrackHistoryStatisticsFragment(
                trackId,
            )
        findTopNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
