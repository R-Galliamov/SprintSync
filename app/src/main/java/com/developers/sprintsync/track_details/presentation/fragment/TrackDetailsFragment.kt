package com.developers.sprintsync.track_details.presentation.fragment

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
import com.developers.sprintsync.core.components.track.presentation.model.UiTrack
import com.developers.sprintsync.core.presentation.view.ConfirmationDialogFragment
import com.developers.sprintsync.core.presentation.view.ConfirmationDialogTag.DELETE
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.databinding.FragmentTrackDetailsBinding
import com.developers.sprintsync.track_details.presentation.view_model.TrackDetailsViewModel
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackDetailsFragment : Fragment() {
    private var _binding: FragmentTrackDetailsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val args: TrackDetailsFragmentArgs by navArgs()

    private val viewModel by viewModels<TrackDetailsViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

    private val deleteTrackDialog by lazy { createDeleteTrackDialog(args.trackId) }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTrackData(args.trackId)
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
        initChartManager(binding.chart)
        setLoadingOverlay()
        observeTrackData()
        setGoToMapButtonListener(args.trackId)
        setBackButtonListener()
        setDeleteTrackButtonListener()
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    private fun observeTrackData() {
        collectFlow(viewModel.state) { state ->
            when (state) {
                TrackDetailsViewModel.DataState.Loading -> {
                    binding.loadingOverlay.show()
                }

                is TrackDetailsViewModel.DataState.Success -> {
                    val track = state.track
                    paceChartManager.setData(state.paceChartData)
                    updateStatisticsValues(track)
                    binding.loadingOverlay.hide()
                }

                is TrackDetailsViewModel.DataState.Error -> {
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

    private fun updateStatisticsValues(track: UiTrack) {
        binding.apply {
            tvDistanceValue.text = track.distanceUnit
            tvDurationValue.text = track.duration
            tvAvgPaceValue.text = track.avgPace
            tvBestPaceValue.text = track.bestPace
            tvCaloriesValue.text = track.calories
        }
    }

    private fun setGoToMapButtonListener(trackId: Int) {
        binding.btGoToMap.setOnClickListener {
            navigateToMapFragment(trackId)
        }
    }

    private fun navigateToMapFragment(trackId: Int) {
        val action = TrackDetailsFragmentDirections.actionTrackDetailsFragmentToMapFragment(trackId)
        findNavController().navigate(action)
    }

    private fun setBackButtonListener() {
        binding.btBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setLoadingOverlay() =
        binding.loadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(context.getString(R.string.loading_data_message))
        }

    private fun setDeleteTrackButtonListener() {
        binding.btDelete.setOnClickListener {
            deleteTrackDialog.show(childFragmentManager, DELETE)
        }
    }

    private fun createDeleteTrackDialog(trackId: Int) =
        ConfirmationDialogFragment().also {
            it.setListener(
                object : ConfirmationDialogFragment.DialogListener {
                    override fun onConfirmed() {
                        viewModel.deleteTrackById(trackId)
                        findNavController().popBackStack()
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
