package com.developers.sprintsync.presentation.workouts_history.workouts_log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.setVisibility
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentTrackHistoryBinding
import com.developers.sprintsync.presentation.main.TabsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for displaying a list of past workout logs.
 */
@AndroidEntryPoint
class WorkoutsLogFragment : Fragment() {
    private var _binding: FragmentTrackHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by viewModels<WorkoutsLogViewModel>()

    private val onRvInteractionListener =
        object : WorkoutsLogAdapter.OnInteractionListener {
            override fun onItemSelected(trackId: Int) {
                navigateToWorkoutDetails(trackId)
            }
        }

    @Inject
    lateinit var rvAdapter: WorkoutsLogAdapter

    @Inject
    lateinit var log: AppLogger

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
        try {
            setupLoadingOverlay()
            setupRecyclerView()
            observeState()
            log.d("WorkoutsLogFragment view created")
        } catch (e: Exception) {
            log.e("Error setting up WorkoutsLogFragment", e)
            showErrorAndBack(log)
        }
    }

    // Observes ViewModel state to update UI
    private fun observeState() {
        observe(viewModel.state) { state ->
            try {
                handleUiState(state)
                log.i("UI state updated: tracks=${state.tracks.size}, loading=${state.showLoadingOverlay}")
            } catch (e: Exception) {
                log.e("Error handling UI state", e)
                showErrorAndBack(log)
            }
        }
    }

    // Updates UI based on ViewModel state
    private fun handleUiState(state: WorkoutsLogViewModel.UiState) {
        rvAdapter.submitList(state.tracks)
        binding.apply {
            viewLoadingOverlay.setVisibility(state.showLoadingOverlay)
            emptyTracksState.root.setVisibility(state.showEmptyTracksPlaceHolder)
            tvErrorMessage.setVisibility(state.showError)
        }
    }

    private fun setupLoadingOverlay() {
        binding.viewLoadingOverlay.apply {
            bindToLifecycle(lifecycle)
        }
        log.d("Loading overlay set up")
    }

    private fun setupRecyclerView() {
        rvAdapter.onInteractionListener = onRvInteractionListener
        binding.rvTracksList.adapter = rvAdapter
        binding.rvTracksList.itemAnimator = null
        log.d("RecyclerView adapter set up")
    }

    private fun navigateToWorkoutDetails(trackId: Int) {
        try {
            val action = TabsFragmentDirections.actionTabsFragmentToTrackDetailsFragment(trackId)
            findTopNavController().navigate(action)
        } catch (e: Exception) {
            log.e("Error navigating to workout details: trackId=$trackId", e)
            showErrorAndBack(log)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
