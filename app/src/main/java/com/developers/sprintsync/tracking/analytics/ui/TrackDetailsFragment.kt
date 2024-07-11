package com.developers.sprintsync.tracking.analytics.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.databinding.FragmentTrackDetailsBinding
import com.developers.sprintsync.tracking.analytics.util.chart.PaceChartManager
import com.developers.sprintsync.tracking.analytics.viewModel.TrackDetailsViewModel
import com.developers.sprintsync.tracking.data.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.data.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.data.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.data.model.track.Track
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
                DistanceMapper.metersToPresentableKilometers(track.distanceMeters, true)
            tvDurationValue.text = TimeMapper.millisToPresentableTime(track.durationMillis)
            tvAvgPaceValue.text = PaceMapper.formatPaceWithTwoDecimals(track.avgPace)
            tvBestPaceValue.text = PaceMapper.formatPaceWithTwoDecimals(track.bestPace)
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
            findNavController().navigateUp()
        }
    }

    // TODO add dialog to confirm delete
    private fun setDeleteTrackButtonListener(trackId: Int) {
        binding.btDelete.setOnClickListener {
            viewModel.deleteTrackById(trackId)
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
