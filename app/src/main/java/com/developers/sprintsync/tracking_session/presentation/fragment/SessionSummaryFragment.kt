package com.developers.sprintsync.tracking_session.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.databinding.FragmentSessionSummaryBinding
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DurationFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiFormatter
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.DistanceUiPattern
import com.developers.sprintsync.tracking_session.presentation.view_model.SessionSummaryViewModel
import com.github.mikephil.charting.charts.LineChart

class SessionSummaryFragment : Fragment() {
    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel by activityViewModels<SessionSummaryViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

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
        setDataObserver()
        setHomeButtonListener()
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
            tvCaloriesValue.text = track.calories.toString() // TODO add formatter
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
        val action =
            SessionSummaryFragmentDirections.actionSessionSummaryFragmentToMapFragment(trackId)
        findNavController().navigate(action)
    }

    // TODO add dialog to confirm delete
    private fun setDeleteTrackButtonListener() {
        binding.btDelete.setOnClickListener {
            getTrackId()?.let { viewModel.deleteTrackById(it) }
            findNavController().navigateUp()
        }
    }

    private fun getTrackId(): Int? = viewModel.lastTrack.value?.id

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
