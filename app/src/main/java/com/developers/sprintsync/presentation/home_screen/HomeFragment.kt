package com.developers.sprintsync.presentation.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentHomeBinding
import com.developers.sprintsync.presentation.home_screen.util.SpannableStyler
import com.developers.sprintsync.presentation.workouts_stats.WorkoutsStatsUiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Displays workout statistics on the home screen.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

    private val spannableStyler by lazy { SpannableStyler(requireContext()) }

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var log: AppLogger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        log.i("HomeFragment created")
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        try {
            observe(viewModel.screenState) { state ->
                updateTotals(state.recordBoardUiModel)
                updateBestResults(state.recordBoardUiModel)
                updateGeneralStatistics(state.workoutsGeneralStats)
            }
        } catch (e: Exception) {
            log.e("Error observing screen state: ${e.message}", e)
        }
    }

    private fun updateTotals(stat: RecordBoardUiModel) {
        try {
            binding.apply {
                tvTotalRunsValue.text = stat.totalWorkouts
                tvDistanceTotalValue.text = stat.totalDistance
                tvTotalKcalValue.text = stat.totalCalories
            }
        } catch (e: Exception) {
            log.e("Error updating totals: ${e.message}", e)
        }

    }

    private fun updateBestResults(stat: RecordBoardUiModel) {
        try {
            binding.apply {
                tvLongestDistanceValue.text = stat.longestDistance
                tvBestPaceValue.text = stat.peakPace
                tvMaxDurationValue.text = spannableStyler.styleDuration(stat.maxDuration)
            }
        } catch (e: Exception) {
            log.e("Error updating best results: ${e.message}", e)
        }
    }

    private fun updateGeneralStatistics(stats: WorkoutsStatsUiModel) {
        try {
            binding.generalStatisticsTable.apply {
                tvWorkoutDaysTotalValue.text = stats.totalWorkoutDays
                tvDistanceTotalValue.text = stats.totalDistance
                tvDurationTotalValue.text = stats.totalDuration
                tvAvgPaceValue.text = stats.avgPace
                tvBestPaceValue.text = stats.peakPace
                tvCaloriesTotalValue.text = stats.totalCalories
            }
        } catch (e: Exception) {
            log.e("Error updating general statistics: ${e.message}", e)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        log.i("HomeFragment destroyed")
    }
}
