package com.developers.sprintsync.presentation.workouts_history.workout_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.navigateBack
import com.developers.sprintsync.presentation.components.view.ConfirmationDialogFragment
import com.developers.sprintsync.presentation.components.view.ConfirmationDialogTag.DELETE
import com.developers.sprintsync.presentation.components.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentTrackDetailsBinding
import com.developers.sprintsync.presentation.components.TrackDisplayModel
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for displaying details of a past workout session.
 */
@AndroidEntryPoint
class WorkoutDetailsFragment : Fragment() {
    private var _binding: FragmentTrackDetailsBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding not initialized" }

    private val args: WorkoutDetailsFragmentArgs by navArgs()

    private val viewModel by viewModels<WorkoutDetailsViewModel>()

    private val paceChart by lazy { PaceChartManager(requireContext()) }

    private val deleteDialog by lazy { createDeleteDialog(args.trackId) }

    @Inject
    lateinit var log: AppLogger

    override fun onStart() {
        super.onStart()
        try {
            viewModel.fetchTrackData(args.trackId)
            log.d("Fetching track data for trackId=${args.trackId}")
        } catch (e: Exception) {
            log.e("Error fetching track data", e)
            showErrorAndBack(log)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        try {
            initChart(binding.chart)
            setupLoadingOverlay()
            observeState()
            setupNavigationButtons()
            setupDeleteButton()

        } catch (e: Exception) {
            log.e("Error setting up fragment", e)
            showErrorAndBack(log)
        }
    }

    // Initializes the pace chart
    private fun initChart(chart: LineChart) {
        try {
            paceChart.setup(chart)
            log.d("Pace chart initialized")
        } catch (e: Exception) {
            log.e("Error initializing pace chart", e)
        }
    }

    // Observes ViewModel state to update UI
    private fun observeState() {
        observe(viewModel.state) { state ->
            try {
                when (state) {
                    is WorkoutDetailsViewModel.State.Loading -> {
                        binding.viewLoadingOverlay.show()
                        log.d("Showing loading state")
                    }

                    is WorkoutDetailsViewModel.State.Success -> {
                        val track = state.track
                        paceChart.setData(state.paceChartData)
                        updateMetrics(track)
                        binding.viewLoadingOverlay.hide()
                        log.i("Loaded track data: trackId=${state.track.id}")
                    }

                    is WorkoutDetailsViewModel.State.Error -> {
                        showErrorAndBack(log)
                        log.e("Error state", state.e)
                    }
                }
            } catch (e: Exception) {
                log.e("Error handling state", e)
                showErrorAndBack(log)
            }
        }
    }

    // Updates UI with metric values
    private fun updateMetrics(track: TrackDisplayModel) {
        binding.apply {
            tvDistanceValue.text = track.distanceUnit
            tvDurationValue.text = track.duration
            tvAvgPaceValue.text = track.avgPace
            tvBestPaceValue.text = track.bestPace
            tvCaloriesValue.text = track.calories
        }
        log.d("Updated metrics: distance=${track.distanceUnit}")
    }

    // Sets up navigation button listeners
    private fun setupNavigationButtons() {
        binding.btnBack.setOnClickListener {
            navigateBack(log)
        }
        binding.btnMap.setOnClickListener {
            navigateToMap(args.trackId)
        }
    }

    private fun navigateToMap(trackId: Int) {
        val action = WorkoutDetailsFragmentDirections.actionTrackDetailsFragmentToMapFragment(trackId)
        findNavController().navigate(action)
        log.i("Navigated to map: trackId=$trackId")
    }

    private fun setupLoadingOverlay() =
        binding.viewLoadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(context.getString(R.string.msg_loading_generic))
        }

    private fun setupDeleteButton() {
        binding.btnDelete.setOnClickListener {
            deleteDialog.show(childFragmentManager, DELETE)
        }
    }

    private fun createDeleteDialog(trackId: Int) =
        ConfirmationDialogFragment().apply {
            setListener(
                object : ConfirmationDialogFragment.DialogListener {
                    override fun onConfirmed() {
                        viewModel.deleteTrack(trackId)
                        navigateBack(log)
                        log.i("Track deleted: trackId=$trackId")
                    }

                    override fun onCancelled() {
                        log.d("Delete cancelled")
                    }
                },
            )
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChart.cleanup()
        log.d("Fragment destroyed")
    }
}
