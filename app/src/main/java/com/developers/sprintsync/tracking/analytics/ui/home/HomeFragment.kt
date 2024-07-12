package com.developers.sprintsync.tracking.analytics.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentHomeBinding
import com.developers.sprintsync.tracking.analytics.model.FormattedStatistics
import com.developers.sprintsync.tracking.analytics.viewModel.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setDataObservers()
    }

    private fun setDataObservers() {
        setStatisticsObservers()
    }

    // TODO add formatted statistics
    private fun setStatisticsObservers() {
        viewModel.statistics.observe(viewLifecycleOwner) { stat ->
            updateTotals(stat)
            updateBestResults(stat)
        }
    }

    private fun updateTotals(stat: FormattedStatistics) {
        binding.apply {
            tvTotalRunsValue.text = stat.runs
            tvTotalDistanceValue.text = stat.totalDistance
            tvTotalKcalValue.text = stat.totalCalories
        }
    }

    private fun updateBestResults(stat: FormattedStatistics) {
        binding.apply {
            tvLongestDistanceValue.text = stat.longestDistance
            tvBestPaceValue.text = stat.bestPace
            tvMaxDurationValue.text = stat.maxDuration
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
