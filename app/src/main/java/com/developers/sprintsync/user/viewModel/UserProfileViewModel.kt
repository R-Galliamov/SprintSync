package com.developers.sprintsync.user.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.developers.sprintsync.databinding.FragmentUserProfileBinding
import com.developers.sprintsync.user.model.FormattedDateRange
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.ui.userProfile.chart.configuration.ChartConfigurationType
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataLoader
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManager
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.manager.ChartManagerImpl
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.ui.userProfile.util.formatter.DateRangeFormatter
import com.github.mikephil.charting.charts.CombinedChart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel
    @Inject
    constructor(
        private val dataLoader: ChartDataLoader,
    ) : ViewModel() {
        private var _chartManager: ChartManager? = null
        private val chartManager get() = checkNotNull(_chartManager) { "ChartManager is not initialized" }

        private var _dateRange: MutableStateFlow<FormattedDateRange> = MutableStateFlow(FormattedDateRange.EMPTY)
        val dateRange get() = _dateRange.asStateFlow()

        private var selectedTab: Int = -1

        fun initChartManager(chart: CombinedChart) {
            _chartManager = ChartManagerImpl(chart)
            initDisplayedDataListener()
            Log.d("UserProfileViewModel", "ChartManager initialized")
        }

        private fun initDisplayedDataListener() {
            CoroutineScope(Dispatchers.IO).launch {
                chartManager.displayedData.collect { data ->
                    if (data.isEmpty()) return@collect
                    _dateRange.update {
                        DateRangeFormatter().formatRange(
                            dataLoader.chartDataSet.referenceTimestamp,
                            data.keys.min(),
                            data.keys.max(),
                        )
                    }
                }
            }
        }

        fun setWeeklyConfiguration() {
            chartManager.presetChartConfiguration(ChartConfigurationType.WEEKLY, dataLoader.chartDataSet.referenceTimestamp)
        }

        fun navigateRange(direction: ChartNavigator.NavigationDirection) = chartManager.navigateRange(direction)

        fun setScroller(binding: FragmentUserProfileBinding) {
            val chartDataSet = dataLoader.chartDataSet

            val tabs =
                listOf(
                    binding.chartTabsScroller.chartTabDistance to chartDataSet.data[Metric.DISTANCE],
                    binding.chartTabsScroller.chartTabDuration to chartDataSet.data[Metric.DURATION],
                    // binding.chartTabCalories to ChartDataLoader.Calories()
                )

            tabs.forEach { (tab, chartData) ->
                tab.setOnClickListener {
                    // Deselect all tabs
                    tabs.forEach { (t, _) -> t.isSelected = false }

                    // Select the clicked tab
                    tab.isSelected = true
                    selectedTab = binding.chartTabsScroller.tabs.indexOfChild(tab)
                    scrollToSelectedTab(binding, tab)

                    // Display the corresponding data
                    chartData?.let {
                        chartManager.displayData(chartData)
                    }
                }
            }
        }

        private fun scrollToSelectedTab(
            binding: FragmentUserProfileBinding,
            selectedTab: View,
        ) {
            binding.chartTabsScroller.root.post {
                val selectedViewCenterX = selectedTab.left + selectedTab.width / 2
                val scrollViewCenterX = binding.chartTabsScroller.tabs.width / 2
                val scrollToX = selectedViewCenterX - scrollViewCenterX

                binding.chartTabsScroller.root.smoothScrollTo(scrollToX, 0)
            }
        }

        fun onDestroy() {
            _chartManager = null
        }
    }
