package com.developers.sprintsync.presentation.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.databinding.FragmentHomeBinding
import com.developers.sprintsync.presentation.home_screen.util.SpannableStyler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val spannableStyler by lazy { SpannableStyler(requireContext()) }

    private val viewModel by viewModels<HomeViewModel>()

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
        observeStatistics()
    }

    private fun observeStatistics() {
        collectFlow(viewModel.statistics) { stats ->
            updateTotals(stats)
            updateBestResults(stats)
        }
    }

    private fun updateTotals(stat: WorkoutStatistics) {
        binding.apply {
            tvTotalRunsValue.text = stat.runs
            tvTotalDistanceValue.text = stat.totalDistance
            tvTotalKcalValue.text = stat.totalKiloCalories
        }
    }

    private fun updateBestResults(stat: WorkoutStatistics) {
        binding.apply {
            tvLongestDistanceValue.text = stat.longestDistance
            tvBestPaceValue.text = stat.bestPace
            tvMaxDurationValue.text = spannableStyler.styleDuration(stat.maxDuration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
