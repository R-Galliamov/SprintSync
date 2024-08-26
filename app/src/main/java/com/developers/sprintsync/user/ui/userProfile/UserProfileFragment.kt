package com.developers.sprintsync.user.ui.userProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentUserProfileBinding
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.chart.navigator.RangePosition
import com.developers.sprintsync.user.model.statistics.GeneralStatistics
import com.developers.sprintsync.user.model.statistics.WeeklyStatistics
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManager
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManagerImpl
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.viewModel.UserProfileViewModel
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<UserProfileViewModel>()

    private var _chartManager: ChartManager? = null
    private val chartManager get() = checkNotNull(_chartManager) { "ChartManager is not initialized" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        setScroller()
        initChartManager(binding.progressChart)
        initDataRangeListener()
        initWeeklyStatisticsListener()
        initGeneralStatisticsListener()
        setRangeNavigatingButtons()
        chartManager.presetChartConfiguration(viewModel.chartConfiguration.value)
        initChartDataUpdateEvent()
        initDisplayDataListener()
    }

    private fun initChartManager(chart: CombinedChart) {
        _chartManager = ChartManagerImpl(chart)
    }

    private fun initDisplayDataListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chartManager.displayData.collect {
                    viewModel.onDisplayedDataChanged(it)
                }
            }
        }
    }

    private fun initChartDataUpdateEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chartDataUpdateEvent.collect { event ->
                    if (event == null) return@collect
                    chartManager.displayData(
                        event.metric,
                        event.dailyValues,
                        event.referenceTimestamp,
                    )
                }
            }
        }
    }

    private fun initWeeklyStatisticsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyStatistics.collect { weeklyStatistics ->
                    Log.d(TAG, "WeeklyStatistics updated: $weeklyStatistics")
                    if (weeklyStatistics == WeeklyStatistics.EMPTY) return@collect
                    binding.weeklyStatisticsTable.apply {
                        tvWorkoutsValue.text = weeklyStatistics.workouts
                        tvWorkoutDaysValue.text = weeklyStatistics.workoutDays
                        tvTotalDistanceValue.text = weeklyStatistics.totalDistance
                        tvTotalDurationValue.text = weeklyStatistics.totalDuration
                        tvBestDistanceValue.text = weeklyStatistics.bestDistance
                        tvBestDurationValue.text = weeklyStatistics.bestDuration
                        tvAvgPaceValue.text = weeklyStatistics.avgPace
                        tvBestPaceValue.text = weeklyStatistics.bestPace
                        tvTotalBurnedKcalValue.text = weeklyStatistics.totalCalories
                    }
                }
            }
        }
    }

    private fun initGeneralStatisticsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.generalStatistics.collect { generalStatistics ->
                    if (generalStatistics == GeneralStatistics.EMPTY) return@collect
                    binding.generalStatisticsTable.apply {
                        tvTotalWorkoutsValue.text = generalStatistics.totalWorkoutDays
                        tvMaxWorkoutStreakValue.text = generalStatistics.maxWorkoutStreak
                        tvTotalWorkoutDaysValue.text = generalStatistics.totalWorkoutDays
                        tvTotalDistanceValue.text = generalStatistics.totalDistance
                        tvTotalDurationValue.text = generalStatistics.totalDuration
                        tvAvgPaceValue.text = generalStatistics.avgPace
                        tvBestPaceValue.text = generalStatistics.bestPace
                        tvTotalBurnedKcalValue.text = generalStatistics.totalBurnedKcal
                    }
                }
            }
        }
    }

    private fun initDataRangeListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateRange.collect { dateRange ->
                    binding.progressChartNavigator.apply {
                        // TODO : doesn't work when no last data in range
                        tvDayMonthRange.text = dateRange.dayMonthRange
                        tvYearRange.text = dateRange.yearsRange
                    }
                    updateNavigatingButtonsUI(dateRange.position)
                }
            }
        }
    }

    private fun setRangeNavigatingButtons() {
        binding.progressChartNavigator.btPreviousRange.setOnClickListener {
            chartManager.navigateRange(ChartNavigator.NavigationDirection.PREVIOUS)
        }
        binding.progressChartNavigator.btNextRange.setOnClickListener {
            chartManager.navigateRange(ChartNavigator.NavigationDirection.NEXT)
        }
    }

    private fun updateNavigatingButtonsUI(rangePosition: RangePosition) {
        when (rangePosition) {
            RangePosition.NOT_INITIALIZED -> {
                binding.progressChartNavigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = false
                }
            }

            RangePosition.FIRST -> {
                binding.progressChartNavigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = true
                }
            }

            RangePosition.MIDDLE -> {
                binding.progressChartNavigator.apply {
                    btPreviousRange.isEnabled = true
                    btNextRange.isEnabled = true
                }
            }

            RangePosition.LAST -> {
                binding.progressChartNavigator.apply {
                    btPreviousRange.isEnabled = true
                    btNextRange.isEnabled = false
                }
            }

            RangePosition.ONLY -> {
                binding.progressChartNavigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _chartManager = null
        _binding = null
    }

    private fun setScroller() {
        binding.chartTabsScroller.chartTabDistance.isSelected = true

        val tabs =
            listOf(
                binding.chartTabsScroller.chartTabDistance to Metric.DISTANCE,
                binding.chartTabsScroller.chartTabDuration to Metric.DURATION,
            )

        tabs.forEach { (tab, metric) ->
            tab.setOnClickListener {
                tabs.forEach { (t, _) -> t.isSelected = false }
                tab.isSelected = true
                viewModel.selectMetric(metric)
                scrollToSelectedTab(binding.chartTabsScroller.root, tab)
            }
        }
    }

    private fun scrollToSelectedTab(
        scroller: HorizontalScrollView,
        selectedTab: View,
    ) {
        scroller.post {
            val selectedViewCenterX = selectedTab.left + selectedTab.width / 2
            val scrollViewCenterX = binding.chartTabsScroller.tabs.width / 2
            val scrollToX = selectedViewCenterX - scrollViewCenterX

            binding.chartTabsScroller.root.smoothScrollTo(scrollToX, 0)
        }
    }

    companion object {
        private const val TAG = "My stack: UserProfileFragment"
    }
}
