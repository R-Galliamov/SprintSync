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
import com.developers.sprintsync.core.util.extension.setVisibility
import com.developers.sprintsync.databinding.FragmentTrackHistoryBinding
import com.developers.sprintsync.run_history.presentation.adapter.TrackListAdapter
import com.developers.sprintsync.run_history.presentation.view_model.TrackHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackHistoryFragment : Fragment() {
    private var _binding: FragmentTrackHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by viewModels<TrackHistoryViewModel>()

    private val onRvInteractionListener =
        object : TrackListAdapter.OnInteractionListener {
            override fun onItemSelected(trackId: Int) {
                navigateToTrackDetails(trackId)
            }
        }

    private val rvAdapter by lazy { TrackListAdapter(onRvInteractionListener) }

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

    private fun observeTracksFlow() { // TODO Complete logic
        collectFlow(viewModel.state) { state -> handleUiState(state) }
    }

    private fun handleUiState(state: TrackHistoryViewModel.UiState) {
        rvAdapter.submitList(state.tracks)
        binding.apply {
            loadingOverlay.setVisibility(state.showLoadingOverlay)
            emptyTracksState.root.setVisibility(state.showEmptyTracksPlaceHolder)
            tvErrorMessage.setVisibility(state.showError)
        }
    }

    private fun setLoadingOverlay() {
        binding.loadingOverlay.apply {
            bindToLifecycle(lifecycle)
        }
    }

    private fun setRecyclerViewAdapter() {
        binding.rvTracksList.adapter = rvAdapter
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
