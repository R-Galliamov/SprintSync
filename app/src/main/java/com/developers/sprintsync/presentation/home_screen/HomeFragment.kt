package com.developers.sprintsync.presentation.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.observe
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
        observe(viewModel.statistics) { stats ->
            updateTotals(stats)
            updateBestResults(stats)
        }
    }

    private fun updateTotals(stat: RecordBoardUiModel) {
        binding.apply {
            tvTotalRunsValue.text = stat.totalWorkouts
            tvDistanceTotalValue.text = stat.totalDistance
            tvTotalKcalValue.text = stat.totalCalories
        }
    }

    private fun updateBestResults(stat: RecordBoardUiModel) {
        binding.apply {
            tvLongestDistanceValue.text = stat.longestDistance
            tvBestPaceValue.text = stat.peakPace
            tvMaxDurationValue.text = spannableStyler.styleDuration(stat.maxDuration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
