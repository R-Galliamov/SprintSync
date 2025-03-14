package com.developers.sprintsync.run_history.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.presentation.fragment.TabsFragmentDirections
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.databinding.FragmentTrackHistoryBinding
import com.developers.sprintsync.run_history.presentation.adapter.TrackListAdapter
import com.developers.sprintsync.run_history.presentation.ui_model.UiTrackPreviewWrapper
import com.developers.sprintsync.run_history.presentation.view_model.TrackHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackHistoryFragment : Fragment() {
    private var _binding: FragmentTrackHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by viewModels<TrackHistoryViewModel>()

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
        _binding = FragmentTrackHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setLoadingOverlay()
        setRecyclerViewAdapter()
        observeTracksFlow()
    }

    private fun observeTracksFlow() {
        collectFlow(viewModel.state) { state ->
            setLoadingOverlayVisibility(state)
            setEmptyTracksStateVisibility(state)
            setErrorState(state)

            if (state is TrackHistoryViewModel.DataState.Success) {
                handleSuccessState(state.tracks)
            }
        }
    }

    private fun handleSuccessState(tracks: List<UiTrackPreviewWrapper>) {
        if (adapter.currentList != tracks) {
            adapter.submitList(tracks)
        }
    }

    private fun setErrorState(state: TrackHistoryViewModel.DataState) {
        binding.tvErrorMessage.text = getString(R.string.error_message_data_loading_failed)
        binding.tvErrorMessage.visibility =
            when (state) {
                is TrackHistoryViewModel.DataState.Error -> View.VISIBLE
                else -> View.GONE
            }
    }

    private fun setLoadingOverlayVisibility(state: TrackHistoryViewModel.DataState) {
        binding.loadingOverlay.visibility =
            when (state) {
                TrackHistoryViewModel.DataState.Loading -> View.VISIBLE
                else -> View.GONE
            }
    }

    private fun setEmptyTracksStateVisibility(state: TrackHistoryViewModel.DataState) {
        binding.emptyTracksState.root.visibility =
            when (state) {
                TrackHistoryViewModel.DataState.Empty -> View.VISIBLE
                else -> View.GONE
            }
    }

    private fun setLoadingOverlay() {
        binding.loadingOverlay.apply {
            bindToLifecycle(lifecycle)
        }
    }

    private fun setRecyclerViewAdapter() {
        binding.rvTracksList.adapter = adapter
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
