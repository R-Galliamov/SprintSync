package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackStatisticsBinding
import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.util.chart.PaceChartManager
import com.developers.sprintsync.tracking.viewModel.TrackStatisticsViewModel
import com.github.mikephil.charting.charts.LineChart

class TrackStatisticsFragment : Fragment() {
    private var _binding: FragmentTrackStatisticsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel by activityViewModels<TrackStatisticsViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initChartManager(binding.chart)
        setDataObserver()
        setHomeButtonListener()
        setGoToMapButtonListener()
        setDeleteTrackButtonListener()
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    // TODO include null case
    private fun setDataObserver() {
        viewModel.lastTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                paceChartManager.setData(track.segments)
                updateStatisticsValues(track)
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

    private fun setHomeButtonListener() {
        binding.btHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setGoToMapButtonListener() {
        binding.btGoToMap.setOnClickListener {
            findNavController().navigate(R.id.action_trackStatisticsFragment_to_mapFragment)
        }
    }

    // TODO add dialog to confirm delete
    private fun setDeleteTrackButtonListener() {
        binding.btDelete.setOnClickListener {
            getTrackId()?.let { viewModel.deleteTrackById(it) }
            findNavController().navigateUp()
        }
    }

    private fun getTrackId(): Int? {
        return viewModel.lastTrack.value?.id
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
