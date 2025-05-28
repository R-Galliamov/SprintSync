package com.developers.sprintsync.presentation.workouts_statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.extension.showToast
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentStatisticsBinding
import com.developers.sprintsync.presentation.workouts_statistics.chart.manager.ChartManager
import com.developers.sprintsync.presentation.workouts_statistics.chart.manager.DefaultChartManager
import com.developers.sprintsync.presentation.workouts_statistics.chart.navigator.ChartNavigator
import com.developers.sprintsync.presentation.workouts_statistics.data.GeneralStatistics
import com.developers.sprintsync.presentation.workouts_statistics.data.WeeklyStatistics
import com.developers.sprintsync.presentation.workouts_statistics.util.scroller.ChartTabsScroller
import com.developers.sprintsync.presentation.workouts_statistics.util.scroller.RangeNavigator
import com.github.mikephil.charting.charts.CombinedChart
import javax.inject.Inject

/**
 * Fragment for displaying workout statistics and charts
 */
class WorkoutsStatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<WorkoutsStatisticsViewModel>()

    private var _chartManager: ChartManager? = null
    private val chartManager get() = checkNotNull(_chartManager) { "ChartManager is not initialized" }

    private val tabsScroller = ChartTabsScroller()
    private val rangeNavigator = RangeNavigator()

    @Inject
    lateinit var log: AppLogger

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
        try {
            setupTabsScroller()
            setupChartManager(binding.progressChart)
            setupRangeNavigator()
            setupStateObservers()
            setupNavigationButtons()
            chartManager.presetChart(viewModel.chartConfig.value)
            setupNavigationButtons()
            log.d("WorkoutsStatisticsFragment view created")
        } catch (e: Exception) {
            log.e("Error setting up WorkoutsStatisticsFragment", e)
            showErrorAndBack(log)
        }

    }

    // Sets up the tabs scroller for metric selection
    private fun setupTabsScroller() {
        tabsScroller.setup(binding.viewChartTabs) { metric ->
            viewModel.selectMetric(metric)
            log.i("Selected metric: $metric")
        }
        observe(viewModel.selectedMetric) { metric ->
            tabsScroller.selectMetricTab(metric)
            log.d("Metric tab updated: $metric")
        }
    }

    // Initializes the chart manager
    private fun setupChartManager(chart: CombinedChart) {
        _chartManager = DefaultChartManager(chart)
        log.d("ChartManager initialized")
    }

    // Sets up range navigator and date range observer
    private fun setupRangeNavigator() {
        binding.viewChartNavigator.apply {
            btnPreviousRange.setOnClickListener {
                chartManager.navigateRange(ChartNavigator.NavigationDirection.PREVIOUS)
                log.i("Navigated to previous range")
            }
            btnNextRange.setOnClickListener {
                chartManager.navigateRange(ChartNavigator.NavigationDirection.NEXT)
                log.i("Navigated to next range")
            }
        }

        observe(viewModel.dateRange) { dateRange ->
            binding.viewChartNavigator.apply {
                tvDayMonthRange.text = dateRange.dayMonthRange
                tvYearRange.text = dateRange.yearsRange
            }
            rangeNavigator.updateNavigatingButtonsUI(binding.viewChartNavigator, dateRange.position)
            log.d("Date range updated: ${dateRange.dayMonthRange}")
        }
    }

    // Sets up state observers for statistics, chart data, and goals
    private fun setupStateObservers() {
        // Chart data updates
        observe(viewModel.chartDataUpdate) { event ->
            chartManager.displayData(
                event.metric,
                event.dailyValues,
                event.referenceTimestamp,
            )
            log.i("Chart data updated: metric=${event.metric}")
        }

        // Weekly statistics
        observe(viewModel.weeklyStatistics) { stats ->
            if (stats == WeeklyStatistics.EMPTY) return@observe
            binding.weeklyStatisticsTable.apply {
                tvWorkoutsValue.text = stats.workouts
                tvWorkoutDaysValue.text = stats.workoutDays
                tvDistanceTotalValue.text = stats.totalDistance
                tvDurationTotalValue.text = stats.totalDuration
                tvBestDistanceValue.text = stats.bestDistance
                tvBestDurationValue.text = stats.bestDuration
                tvAvgPaceValue.text = stats.avgPace
                tvBestPaceValue.text = stats.bestPace
                tvCaloriesTotalValue.text = stats.totalCalories
            }
            log.d("Weekly statistics updated")
        }

        // General statistics
        observe(viewModel.generalStatistics) { stats ->
            if (stats == GeneralStatistics.EMPTY) return@observe
            binding.generalStatisticsTable.apply {
                tvWorkoutsTotalValue.text = stats.totalWorkouts
                tvWorkoutStreakMaxValue.text = stats.maxWorkoutStreak
                tvWorkoutDaysTotalValue.text = stats.totalWorkoutDays
                tvDistanceTotalValue.text = stats.totalDistance
                tvDurationTotalValue.text = stats.totalDuration
                tvAvgPaceValue.text = stats.avgPace
                tvBestPaceValue.text = stats.bestPace
                tvCaloriesTotalValue.text = stats.totalCalories
            }
            log.d("General statistics updated")
        }

        // Displayed data feedback
        observe(chartManager.displayData) {
            viewModel.onDisplayedDataChanged(it)
            log.d("Displayed chart data changed")
        }

        // Daily goals update date
        observe(viewModel.dailyGoalsUpdateDate) { date ->
            binding.btnUpdateGoals.tvUpdateTime.text = date
            log.d("Daily goals update timestamp: $date")

        }

    }

    private fun setupNavigationButtons() {
        binding.btnUpdateGoals.root.setOnClickListener {
            try {
                findTopNavController().navigate(R.id.action_tabsFragment_to_updateGoalsFragment)
                log.i("Navigated to update goals")
            } catch (e: Exception) {
                log.e("Error navigating to update goals", e)
                showToast("Navigation error")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _chartManager = null
        _binding = null
        log.d("Fragment view destroyed")
    }

}
