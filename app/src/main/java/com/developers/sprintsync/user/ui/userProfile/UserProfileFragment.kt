package com.developers.sprintsync.user.ui.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentUserProfileBinding
import com.developers.sprintsync.user.model.chart.DailyDataPoint
import com.developers.sprintsync.user.model.chart.WeekDay
import com.developers.sprintsync.user.model.chart.WeeklyChartData
import com.developers.sprintsync.user.ui.userProfile.util.chart.ChartRenderer

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val chartRenderer: ChartRenderer by lazy { ChartRenderer(binding.weeklyChart) }

    private var selectedTab = -1

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
        setScroller()

        val point1 = DailyDataPoint.Present(WeekDay.MONDAY, 2f, 1.9f)
        val point2 = DailyDataPoint.Present(WeekDay.TUESDAY, 2f, 2.3f)
        val point3 = DailyDataPoint.Present(WeekDay.WEDNESDAY, 2f, 2.1f)
        val point4 = DailyDataPoint.Present(WeekDay.THURSDAY, 2f, 2.0f)
        val point5 = DailyDataPoint.Missing(WeekDay.FRIDAY, 2f)
        val point6 = DailyDataPoint.Present(WeekDay.SATURDAY, 2f, 1.8f)
        val point7 = DailyDataPoint.Present(WeekDay.SUNDAY, 2f, 3f)

        // val point6 = DailyDataPoint(WeekDay.SATURDAY, 150f, 160f)
        // val point7 = DailyDataPoint(WeekDay.SUNDAY, 160f, 170f)

        val list = listOf(point1, point2, point3, point4, point5, point6, point7)

        val testData = WeeklyChartData("Test", list)
        chartRenderer.renderData(testData)
    }

    private fun setScroller() {
        binding.chartTabDistance.setOnClickListener {
            binding.chartTabDistance.isSelected = true
            binding.chartTabDuration.isSelected = false
            binding.chartTabCalories.isSelected = false

            selectedTab = binding.chartTabs.indexOfChild(binding.chartTabDistance)
            scrollToSelectedTab(binding.chartTabDistance)
        }
        binding.chartTabDuration.setOnClickListener {
            binding.chartTabDistance.isSelected = false
            binding.chartTabDuration.isSelected = true
            binding.chartTabCalories.isSelected = false

            selectedTab = binding.chartTabs.indexOfChild(binding.chartTabDuration)
            scrollToSelectedTab(binding.chartTabDuration)
        }
        binding.chartTabCalories.setOnClickListener {
            binding.chartTabDistance.isSelected = false
            binding.chartTabDuration.isSelected = false
            binding.chartTabCalories.isSelected = true

            selectedTab = binding.chartTabs.indexOfChild(binding.chartTabCalories)
            scrollToSelectedTab(binding.chartTabCalories)
        }
    }

    private fun scrollToSelectedTab(selectedTab: View) {
        binding.chartTabsScroller.post {
            val selectedViewCenterX = selectedTab.left + selectedTab.width / 2
            val scrollViewCenterX = binding.chartTabs.width / 2
            val scrollToX = selectedViewCenterX - scrollViewCenterX

            binding.chartTabsScroller.smoothScrollTo(scrollToX, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
