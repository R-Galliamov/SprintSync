package com.developers.sprintsync.track_details.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.databinding.FragmentTrackDetailsBinding
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.track_details.presentation.view_model.TrackDetailsViewModel
import com.github.mikephil.charting.charts.LineChart

class TrackDetailsFragment : Fragment() {
    private var _binding: FragmentTrackDetailsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val args: TrackDetailsFragmentArgs by navArgs()

    private val viewModel by activityViewModels<TrackDetailsViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

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
        setDeleteTrackButtonListener(args.trackId)
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    // TODO include null case
    private fun setDataObserver(trackId: Int) {
        viewModel.getTrackById(trackId).observe(viewLifecycleOwner) { track ->
            track?.let {
                paceChartManager.setData(track.segments)
                updateStatisticsValues(track)
                setGoToMapButtonListener(track.id)
            }
        }
    }

    private fun updateStatisticsValues(track: Track) {
        binding.apply {
            tvDistanceValue.text =
                DistanceUiFormatter.format(track.distanceMeters, DistanceUiPattern.WITH_UNIT)
            tvDurationValue.text = DurationFormatter.formatToHhMmSs(track.durationMillis)
            tvAvgPaceValue.text = PaceFormatter.formatPaceWithTwoDecimals(track.avgPace)
            tvBestPaceValue.text = PaceFormatter.formatPaceWithTwoDecimals(track.bestPace)
            tvCaloriesValue.text = track.calories.toString()
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

    // TODO add dialog to confirm delete
    private fun setDeleteTrackButtonListener(trackId: Int) {
        binding.btDelete.setOnClickListener {
            viewModel.deleteTrackById(trackId)
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
