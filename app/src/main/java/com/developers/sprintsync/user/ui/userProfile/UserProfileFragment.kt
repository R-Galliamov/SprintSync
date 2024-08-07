package com.developers.sprintsync.user.ui.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentUserProfileBinding
import com.developers.sprintsync.user.ui.userProfile.chart.data.ChartDataLoader
import com.developers.sprintsync.user.ui.userProfile.chart.ChartRenderer

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

        val testData1 = ChartDataLoader().testData1
        val testData2 = ChartDataLoader().testData2
        val testData3 = ChartDataLoader().testData3
        val testDataGeneral = ChartDataLoader().testDataGeneral

        chartRenderer.renderChart(testDataGeneral)
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
