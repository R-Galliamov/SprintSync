package com.developers.sprintsync.statistics.presentation.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentStatisticsBinding
import com.developers.sprintsync.global.util.extension.findTopNavController
import com.developers.sprintsync.statistics.domain.statistics.GeneralStatistics
import com.developers.sprintsync.statistics.domain.statistics.WeeklyStatistics
import com.developers.sprintsync.statistics.domain.chart.manager.ChartManager
import com.developers.sprintsync.statistics.domain.chart.manager.DefaultChartManager
import com.developers.sprintsync.statistics.domain.chart.navigator.ChartNavigator
import com.developers.sprintsync.statistics.presentation.statistics.util.scroller.ChartTabsScrollerManager
import com.developers.sprintsync.statistics.presentation.statistics.util.scroller.RangeNavigatingManager
import com.developers.sprintsync.statistics.presentation.viewModel.StatisticsViewModel
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<StatisticsViewModel>()

    private var _chartManager: ChartManager? = null
    private val chartManager get() = checkNotNull(_chartManager) { "ChartManager is not initialized" }

    private val tabsScroller = ChartTabsScrollerManager()
    private val rangeNavigator = RangeNavigatingManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        tabsScroller.setScroller(binding.chartTabsScroller) { metric ->
            viewModel.selectMetric(metric)
        }
        initSelectedMetricListener()
        initChartManager(binding.progressChart)
        initDataRangeListener()
        initWeeklyStatisticsListener()
        initGeneralStatisticsListener()
        setRangeNavigatingButtons()
        chartManager.presetChartConfiguration(viewModel.chartConfiguration.value)
        initChartDataUpdateEvent()
        initDisplayDataListener()
        initDailyGoalsUpdateTimestampListener()
        setUpdateGoalsButtonListener()
    }

    private fun initSelectedMetricListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedMetric.collect { metric ->
                    tabsScroller.selectMetricTab(metric)
                }
            }
        }
    }

    private fun initChartManager(chart: CombinedChart) {
        _chartManager = DefaultChartManager(chart)
    }

    private fun initDataRangeListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateRange.collect { dateRange ->
                    binding.progressChartNavigator.apply {
                        tvDayMonthRange.text = dateRange.dayMonthRange
                        tvYearRange.text = dateRange.yearsRange
                    }
                    rangeNavigator.updateNavigatingButtonsUI(binding.progressChartNavigator, dateRange.position)
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
                        tvTotalWorkoutsValue.text = generalStatistics.totalWorkouts
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

    private fun initDisplayDataListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chartManager.displayData.collect {
                    viewModel.onDisplayedDataChanged(it)
                }
            }
        }
    }

    private fun initDailyGoalsUpdateTimestampListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyGoalsUpdateDate.collect { timestamp ->
                    binding.btUpdateGoals.tvUpdateTime.text = timestamp.toString()
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

    private fun setUpdateGoalsButtonListener() {
        binding.btUpdateGoals.root.setOnClickListener {
            findTopNavController().navigate(R.id.action_tabsFragment_to_updateGoalsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _chartManager = null
        _binding = null
    }

    companion object {
        private const val TAG = "My stack: UserProfileFragment"
    }
}
