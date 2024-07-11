package com.developers.sprintsync.tracking.analytics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackListBinding
import com.developers.sprintsync.global.ui.fragment.TabsFragmentDirections
import com.developers.sprintsync.global.util.extension.findTopNavController
import com.developers.sprintsync.tracking.analytics.adapter.TrackListAdapter
import com.developers.sprintsync.tracking.analytics.viewModel.TrackListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackListFragment : Fragment() {
    private var _binding: FragmentTrackListBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<TrackListViewModel>()

    private val onInteractionListener =
        object : TrackListAdapter.OnInteractionListener {
            override fun onItemSelected(trackId: Int) {
                navigateToTrackDetails(trackId)
            }
        }

    private val adapter by lazy { TrackListAdapter(onInteractionListener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackListBinding.inflate(inflater, container, false)
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

    private fun navigateToTrackDetails(trackId: Int) {
        val action =
            TabsFragmentDirections.actionTabsFragmentToTrackDetailsFragment(
                trackId,
            )
        findTopNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
