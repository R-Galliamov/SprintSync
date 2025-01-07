package com.developers.sprintsync.run_history.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackListBinding
import com.developers.sprintsync.core.presentation.fragment.TabsFragmentDirections
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.run_history.presentation.adapter.TrackListAdapter
import com.developers.sprintsync.run_history.presentation.view_model.RunHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunHistoryFragment : Fragment() {
    private var _binding: FragmentTrackListBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<RunHistoryViewModel>()

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
                updateEmptyLogsStateUiVisibility(false)
            } else {
                updateEmptyLogsStateUiVisibility(true)
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

    private fun updateEmptyLogsStateUiVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.emptyLogsStateView.visibility = View.VISIBLE
            false -> binding.emptyLogsStateView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
