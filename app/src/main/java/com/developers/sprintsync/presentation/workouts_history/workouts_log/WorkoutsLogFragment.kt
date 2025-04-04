package com.developers.sprintsync.presentation.workouts_history.workouts_log

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutsLogFragment : Fragment() {
    private var _binding: FragmentTrackHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by viewModels<WorkoutsLogViewModel>()

    private val onRvInteractionListener =
        object : WorkoutsLogAdapter.OnInteractionListener {
            override fun onItemSelected(trackId: Int) {
                navigateToTrackDetails(trackId)
            }
        }

    private val rvAdapter by lazy { WorkoutsLogAdapter(onRvInteractionListener) }

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
        collectFlow(viewModel.state) { state -> handleUiState(state) }
    }

    private fun handleUiState(state: WorkoutsLogViewModel.UiState) {
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
        binding.rvTracksList.itemAnimator = null
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
