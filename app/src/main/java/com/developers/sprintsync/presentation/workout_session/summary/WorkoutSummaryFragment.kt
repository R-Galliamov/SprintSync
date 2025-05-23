package com.developers.sprintsync.presentation.workout_session.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.R
import com.developers.sprintsync.core.presentation.view.ConfirmationDialogFragment
import com.developers.sprintsync.core.presentation.view.ConfirmationDialogTag.DELETE
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.databinding.FragmentSessionSummaryBinding
import com.developers.sprintsync.presentation.components.TrackDisplayModel
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutSummaryFragment : Fragment() {
    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val args: WorkoutSummaryFragmentArgs by navArgs()

    private val viewModel by viewModels<WorkoutSummaryViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

    private val deleteConfirmationDialog by lazy { createDeleteTrackDialog(args.trackId) }

    override fun onStart() {
        super.onStart()
        viewModel.fetchSessionData(args.trackId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSessionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initChartManager(binding.chart)
        setLoadingOverlay()
        setDataObserver()
        setHomeButtonListener()
        setDeleteTrackButtonListener()
        setGoToMapButtonListener(args.trackId)
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    private fun setDataObserver() {
        collectFlow(viewModel.state) { state ->
            when (state) {
                is WorkoutSummaryViewModel.DataState.Loading -> {
                    binding.loadingOverlay.show()
                }

                is WorkoutSummaryViewModel.DataState.Success -> {
                    binding.loadingOverlay.hide()
                    updateStatisticsValues(state.track)
                    paceChartManager.setData(state.paceChartData)
                }

                is WorkoutSummaryViewModel.DataState.Error -> {
                    Toast
                        .makeText(
                            requireContext(),
                            getString(R.string.unexpected_error_message),
                            Toast.LENGTH_SHORT,
                        ).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun updateStatisticsValues(track: TrackDisplayModel) {
        binding.apply {
            tvDistanceValue.text = track.distanceUnit
            tvDurationValue.text = track.duration
            tvAvgPaceValue.text = track.avgPace
            tvBestPaceValue.text = track.bestPace
            tvCaloriesValue.text = track.calories
        }
    }

    private fun setHomeButtonListener() {
        binding.btHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setGoToMapButtonListener(trackId: Int) {
        binding.btGoToMap.setOnClickListener {
            navigateToMapFragment(trackId)
        }
    }

    private fun navigateToMapFragment(trackId: Int) {
        val action = WorkoutSummaryFragmentDirections.actionSessionSummaryFragmentToMapFragment(trackId)
        findNavController().navigate(action)
    }

    private fun setDeleteTrackButtonListener() {
        binding.btDelete.setOnClickListener {
            deleteConfirmationDialog.show(childFragmentManager, DELETE)
        }
    }

    private fun setLoadingOverlay() =
        binding.loadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(context.getString(R.string.completing_track_message))
        }

    private fun createDeleteTrackDialog(trackId: Int) =
        ConfirmationDialogFragment().also {
            it.setListener(
                object : ConfirmationDialogFragment.DialogListener {
                    override fun onConfirmed() {
                        viewModel.deleteTrackById(trackId)
                        findNavController().navigateUp()
                    }

                    override fun onCancelled() {
                        // NO-OP
                    }
                },
            )
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
