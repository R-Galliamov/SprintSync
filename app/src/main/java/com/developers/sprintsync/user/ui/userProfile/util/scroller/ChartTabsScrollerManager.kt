package com.developers.sprintsync.user.ui.userProfile.util.scroller

import android.view.View
import com.developers.sprintsync.databinding.ProgressChartTabsScrollerBinding
import com.developers.sprintsync.user.model.chart.chartData.Metric

class ChartTabsScrollerManager {
    fun setScroller(
        scrollerView: ProgressChartTabsScrollerBinding,
        onTabSelected: (metric: Metric) -> Unit,
    ) {
        scrollerView.chartTabDistance.isSelected = true

        val tabs: MutableList<Pair<View, Metric>> = mutableListOf()

        Metric.entries.forEach { metric ->
            with(scrollerView) {
                when (metric) {
                    Metric.DISTANCE -> tabs.add(chartTabDistance to Metric.DISTANCE)
                    Metric.DURATION -> tabs.add(chartTabDuration to Metric.DURATION)
                    Metric.CALORIES -> tabs.add(chartTabCalories to Metric.CALORIES)
                }
            }
        }

        tabs.forEach { (tab, metric) ->
            tab.setOnClickListener {
                tabs.forEach { (t, _) -> t.isSelected = false }
                tab.isSelected = true
                onTabSelected(metric)
                scrollToSelectedTab(scrollerView, tab)
            }
        }
    }

    private fun scrollToSelectedTab(
        scrollerView: ProgressChartTabsScrollerBinding,
        selectedTab: View,
    ) {
        scrollerView.root.post {
            val selectedViewCenterX = calculateTabCenterX(selectedTab)
            val scrollViewCenterX = calculateScrollerCenterX(scrollerView)
            val scrollToX = selectedViewCenterX - scrollViewCenterX

            scrollerView.root.smoothScrollTo(scrollToX, SCROLL_TO_Y)
        }
    }

    private fun calculateTabCenterX(view: View): Int = view.left + view.width / DIVISOR_FOR_CENTER

    private fun calculateScrollerCenterX(view: ProgressChartTabsScrollerBinding): Int = view.tabs.width / DIVISOR_FOR_CENTER

    companion object {
        private const val DIVISOR_FOR_CENTER = 2
        private const val SCROLL_TO_Y = 0
    }
}
