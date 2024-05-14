package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.databinding.FragmentTrackStatisticsBinding
import com.developers.sprintsync.tracking.model.TEST_SEGMENTS
import com.developers.sprintsync.tracking.util.chart.PaceChartManager
import com.developers.sprintsync.tracking.viewModel.TrackingViewModel
import com.github.mikephil.charting.charts.LineChart

class TrackStatisticsFragment : Fragment() {
    private var _binding: FragmentTrackStatisticsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel by activityViewModels<TrackingViewModel>()

    private var paceChartManager: PaceChartManager? = null

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
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager = PaceChartManager(requireContext())
        paceChartManager?.initialize(chart)
    }

    // TODO include null case
    private fun setDataObserver() {
        viewModel.track.observe(viewLifecycleOwner) {
            paceChartManager?.setData(TEST_SEGMENTS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager?.cleanup()
        paceChartManager = null
    }
}
