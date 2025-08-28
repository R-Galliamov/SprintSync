package com.developers.sprintsync.presentation.workouts_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.databinding.FragmentStatisticsBinding
import com.developers.sprintsync.presentation.workouts_stats.chart.manager.ChartManager
import com.developers.sprintsync.presentation.workouts_stats.chart.manager.DefaultChartManager
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.ChartNavigator
import com.developers.sprintsync.presentation.workouts_stats.util.scroller.ChartTabsScroller
import com.developers.sprintsync.presentation.workouts_stats.util.scroller.RangeNavigator
import com.github.mikephil.charting.charts.CombinedChart
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for displaying workout statistics and charts
 */
@AndroidEntryPoint
class WorkoutsStatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

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
            chartManager.presetChart(viewModel.chartConfig.value)
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
        observe(viewModel.workoutsWeekStats) { stats ->
            binding.weeklyStatisticsTable.apply {
                tvWorkoutsValue.text = stats.totalWorkouts
                tvWorkoutDaysValue.text = stats.totalWorkoutDays
                tvDistanceTotalValue.text = stats.totalDistance
                tvDurationTotalValue.text = stats.totalDuration
                tvBestDistanceValue.text = stats.longestDistance
                tvBestDurationValue.text = stats.longestDuration
                tvAvgPaceValue.text = stats.avgPace
                tvBestPaceValue.text = stats.peakPace
                tvCaloriesTotalValue.text = stats.totalCalories
            }
            log.d("Weekly statistics updated")
        }

        // Displayed data feedback
        observe(chartManager.displayData) {
            viewModel.onDisplayedDataChanged(it)
            log.d("Displayed chart data changed")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _chartManager = null
        _binding = null
        log.d("Fragment view destroyed")
    }

}
