package com.developers.sprintsync.tracking_session.presentation.session_summary

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
import com.developers.sprintsync.core.presentation.view.pace_chart.PaceChartManager
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.databinding.FragmentSessionSummaryBinding
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionSummaryFragment : Fragment() {
    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val args: SessionSummaryFragmentArgs by navArgs()

    private val viewModel by viewModels<SessionSummaryViewModel>()

    private val paceChartManager by lazy { PaceChartManager(requireContext()) }

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
        setDeleteTrackButtonListener(args.trackId)
        setGoToMapButtonListener(args.trackId)
    }

    private fun initChartManager(chart: LineChart) {
        paceChartManager.initialize(chart)
    }

    private fun setDataObserver() {
        collectFlow(viewModel.state) { state ->
            when (state) {
                is SessionSummaryState.Loading -> {
                    binding.loadingOverlay.show()
                }

                is SessionSummaryState.Success -> {
                    binding.loadingOverlay.hide()
                    updateStatisticsValues(state.track)
                    paceChartManager.setData(state.paceChartData)
                }

                is SessionSummaryState.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun updateStatisticsValues(track: UiTrack) {
        binding.apply {
            tvDistanceValue.text = track.distance
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
        val action =
            SessionSummaryFragmentDirections.actionSessionSummaryFragmentToMapFragment(trackId)
        findNavController().navigate(action)
    }

    // TODO add dialog to confirm delete
    private fun setDeleteTrackButtonListener(trackId: Int) {
        binding.btDelete.setOnClickListener {
            viewModel.deleteTrackById(trackId)
            findNavController().navigateUp()
        }
    }

    private fun setLoadingOverlay() =
        binding.loadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(context.getString(R.string.completing_track_message))
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        paceChartManager.cleanup()
    }
}
