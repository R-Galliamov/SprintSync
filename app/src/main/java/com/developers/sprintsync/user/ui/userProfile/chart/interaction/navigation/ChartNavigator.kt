package com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation

import android.util.Log
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.BarAnimator
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

sealed class RangeLimitsState {
    data object Undefined : RangeLimitsState()

    data class Loaded(
        val chartRange: Int,
        val maxRangeIndex: Int,
    ) : RangeLimitsState()
}

sealed class ViewportIndicesState {
    data object Undefined : ViewportIndicesState()

    data class Loaded(
        val displayedRangeIndex: Int,
        val firstDisplayedEntryIndex: Int,
    ) : ViewportIndicesState()
}

class ViewportIndicesStateFactory {
    companion object {
        fun create(
            rangeIndex: Int,
            chartRange: Int,
        ) = ViewportIndicesState.Loaded(
            displayedRangeIndex = rangeIndex,
            firstDisplayedEntryIndex = rangeIndex * chartRange,
        )

        fun createDefault() =
            ViewportIndicesState.Loaded(
                displayedRangeIndex = 0,
                firstDisplayedEntryIndex = 0,
            )
    }
}

class ChartNavigator(
    private val chart: CombinedChart,
) {
    private val barAnimator = BarAnimator(chart)

    private val _rangeLimits: MutableStateFlow<RangeLimitsState> = MutableStateFlow(RangeLimitsState.Undefined)
    val rangeLimits = _rangeLimits.asStateFlow()

    private val _indices: MutableStateFlow<ViewportIndicesState> = MutableStateFlow(ViewportIndicesState.Undefined)
    val indices = _indices.asStateFlow()

    fun invalidate() {
        val range = chart.visibleXRange.roundToInt()
        val maxRangeIndex = (chart.data.maxEntryCountSet.entryCount / range) - INDEX_OFFSET
        _rangeLimits.value = RangeLimitsState.Loaded(range, maxRangeIndex)

        if (indices.value is ViewportIndicesState.Undefined) {
            Log.d("My stack: ChartNavigator", "DEFAULT INDICES ")
            _indices.value = ViewportIndicesStateFactory.createDefault()
        }
    }

    // TODO write index converter to x position

    /**
     * Displays a range of data in the chart.
     * Pass [Int.MAX_VALUE] to display last range.
     * Pass [Int.MIN_VALUE] to display first range.
     */
    fun displayRange(rangeIndex: Int) {
        if (rangeLimits.value is RangeLimitsState.Undefined) return
        if (indices.value is ViewportIndicesState.Undefined) return

        val loadedIndices = indices.value as ViewportIndicesState.Loaded
        val currentRangeIndex = loadedIndices.displayedRangeIndex

        val shiftRanges = rangeIndex - currentRangeIndex
        updateIndices(shiftRanges)

        val updatedIndices = indices.value as ViewportIndicesState.Loaded
        val firstIndexToBeDisplayed = updatedIndices.firstDisplayedEntryIndex
        chart.moveViewToX(firstIndexToBeDisplayed - 0.5f)
    }

    fun navigateRange(
        direction: NavigationDirection,
        shiftRanges: Int = DEFAULT_RANGE_SHIFT,
    ) {
        if (rangeLimits.value is RangeLimitsState.Undefined) return
        if (indices.value is ViewportIndicesState.Undefined) return

        val loadedIndices = indices.value as ViewportIndicesState.Loaded
        val loadedLimits = rangeLimits.value as RangeLimitsState.Loaded

        val range = loadedLimits.chartRange
        val maxRangeIndex = loadedLimits.maxRangeIndex

        val minEntryIndex = loadedIndices.firstDisplayedEntryIndex
        val rangeIndex = loadedIndices.displayedRangeIndex

        when (direction) {
            NavigationDirection.PREVIOUS -> {
                if (minEntryIndex > FIRST_INDEX) {
                    barAnimator.moveBars(minEntryIndex, -range * shiftRanges)
                    updateIndices(-shiftRanges)
                }
            }

            NavigationDirection.NEXT -> {
                if (rangeIndex < maxRangeIndex) {
                    barAnimator.moveBars(minEntryIndex, range * shiftRanges)
                    updateIndices(shiftRanges)
                }
            }
        }
    }

    private fun updateIndices(shiftRanges: Int) {
        if (rangeLimits.value is RangeLimitsState.Undefined) return
        if (indices.value is ViewportIndicesState.Undefined) return

        val loadedIndices = indices.value as ViewportIndicesState.Loaded
        val loadedLimits = rangeLimits.value as RangeLimitsState.Loaded

        val range = loadedLimits.chartRange
        val maxRangeIndex = loadedLimits.maxRangeIndex

        val currentRangeIndex = loadedIndices.displayedRangeIndex
        val newRangeIndex =
            if (currentRangeIndex + shiftRanges < FIRST_INDEX) {
                FIRST_INDEX
            } else if (currentRangeIndex + shiftRanges > maxRangeIndex) {
                maxRangeIndex
            } else {
                currentRangeIndex + shiftRanges
            }

        val newIndices = ViewportIndicesStateFactory.create(rangeIndex = newRangeIndex, chartRange = range)
        _indices.value = newIndices
    }

    enum class NavigationDirection {
        PREVIOUS,
        NEXT,
    }

    companion object {
        private const val FIRST_INDEX = 0
        private const val INDEX_OFFSET = 1

        private const val DEFAULT_RANGE_SHIFT = 1

        private const val TAG = "My stack: ChartNavigator"
    }
}
