package com.developers.sprintsync.statistics.presentation.statistics.util.scroller

import android.view.View
import com.developers.sprintsync.databinding.ProgressChartTabsScrollerBinding
import com.developers.sprintsync.statistics.domain.chart.data.Metric

class ChartTabsScrollerManager {
    private val tabs = mutableMapOf<Metric, View>()
    private var scrollerView: ProgressChartTabsScrollerBinding? = null

    fun setScroller(
        scrollerView: ProgressChartTabsScrollerBinding,
        onTabSelected: (metric: Metric) -> Unit,
    ) {
        this.scrollerView = scrollerView
        initTabs(scrollerView)
        setOnTabClickListener(onTabSelected)
    }

    fun selectMetricTab(metric: Metric) {
        val tab = tabs[metric]
        tab?.let { updateTabsUi(tab) }
    }

    private fun initTabs(scrollerView: ProgressChartTabsScrollerBinding) {
        Metric.entries.forEach { metric ->
            val tab =
                when (metric) {
                    Metric.DISTANCE -> scrollerView.chartTabDistance
                    Metric.DURATION -> scrollerView.chartTabDuration
                    Metric.CALORIES -> scrollerView.chartTabCalories
                }
            tabs[metric] = tab
        }
    }

    private fun setOnTabClickListener(onTabSelected: (metric: Metric) -> Unit) {
        tabs.forEach { (metric, tab) ->
            tab.setOnClickListener {
                updateTabsUi(tab)
                onTabSelected(metric)
            }
        }
    }

    private fun updateTabsUi(selectedTab: View) {
        tabs.values.forEach { t -> t.isSelected = false }
        selectedTab.isSelected = true
        scrollerView?.let { scrollToSelectedTab(it, selectedTab) }
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
