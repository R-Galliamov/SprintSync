package com.developers.sprintsync.track_details.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationUiPattern
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.presentation.view.ConfirmationDialogFragment
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.databinding.FragmentTrackDetailsBinding
import com.developers.sprintsync.track_details.presentation.view_model.TrackDetailsViewModel
import com.github.mikephil.charting.charts.LineChart
import java.util.Locale

class TrackDetailsFragment : Fragment() {
    private var _binding: FragmentTrackDetailsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val args: TrackDetailsFragmentArgs by navArgs()

    private val viewModel by activityViewModels<TrackDetailsViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

    private val deleteTrackDialog by lazy { createDeleteTrackDialog(args.trackId) }

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
        setDataObserver(args.trackId)
        setBackButtonListener()
        setDeleteTrackButtonListener()
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    // TODO include null case
    private fun setDataObserver(trackId: Int) {
        viewModel.getTrackById(trackId).observe(viewLifecycleOwner) { track ->
            track?.let {
                // paceChartManager.setData(track.segments)
                updateStatisticsValues(track)
                setGoToMapButtonListener(track.id)
            }
        }
    }

    // TODO move to view model
    private fun updateStatisticsValues(track: Track) {
        binding.apply {
            tvDistanceValue.text =
                DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            tvDurationValue.text = DurationUiFormatter.format(track.durationMillis, DurationUiPattern.HH_MM_SS)
            tvAvgPaceValue.text = PaceFormatter.formatPaceWithTwoDecimals(track.avgPace)
            tvBestPaceValue.text = PaceFormatter.formatPaceWithTwoDecimals(track.bestPace)
            tvCaloriesValue.text = String.format(Locale.getDefault(), track.calories.toString()) // TODO add formatter
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

    private fun setDeleteTrackButtonListener() {
        binding.btDelete.setOnClickListener {
            deleteTrackDialog.show(childFragmentManager, "DeleteTrackDialog")
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
