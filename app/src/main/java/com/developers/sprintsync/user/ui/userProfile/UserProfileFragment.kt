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
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.viewModel.UserProfileViewModel
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<UserProfileViewModel>()

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

        viewModel.initChartManager(binding.progressChart)
        setScroller()

        initDataRangeListener()

        setRangeNavigatingButtons()
    }

    private fun initDataRangeListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateRange.collect { dateRange ->
                    binding.progressChartNavigator.apply {
                        // TODO : doesn't work when no last data in range
                        tvDayMonthRange.text = dateRange.dayMonthRange
                        tvYearRange.text = dateRange.yearsRange
                        Log.d(TAG, "RangePosition ${dateRange.position}")
                    }
                    updateNavigatingButtonsUI(dateRange.position)
                }
            }
        }
    }

    private fun setRangeNavigatingButtons() {
        binding.progressChartNavigator.btPreviousRange.setOnClickListener {
            viewModel.navigateRange(ChartNavigator.NavigationDirection.PREVIOUS)
        }
        binding.progressChartNavigator.btNextRange.setOnClickListener {
            viewModel.navigateRange(ChartNavigator.NavigationDirection.NEXT)
        }
    }

    private fun updateNavigatingButtonsUI(rangePosition: RangePosition) {
        when (rangePosition) {
            RangePosition.NOT_INITIALIZED -> {
                binding.progressChartNavigator.btPreviousRange.isEnabled = false
                binding.progressChartNavigator.btNextRange.isEnabled = false
            }

            RangePosition.FIRST -> {
                binding.progressChartNavigator.btPreviousRange.isEnabled = false
                binding.progressChartNavigator.btNextRange.isEnabled = true
            }

            RangePosition.MIDDLE -> {
                binding.progressChartNavigator.btPreviousRange.isEnabled = true
                binding.progressChartNavigator.btNextRange.isEnabled = true
            }

            RangePosition.LAST -> {
                binding.progressChartNavigator.btPreviousRange.isEnabled = true
                binding.progressChartNavigator.btNextRange.isEnabled = false
            }

            RangePosition.ONLY -> {
                binding.progressChartNavigator.btPreviousRange.isEnabled = false
                binding.progressChartNavigator.btNextRange.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroy()
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
        private const val TAG = "My stack, UserProfileFragment"
    }
}
