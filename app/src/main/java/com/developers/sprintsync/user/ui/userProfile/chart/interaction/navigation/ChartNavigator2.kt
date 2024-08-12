package com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation

import android.util.Log
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.BarAnimator
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class RangeLimits(
    val chartRange: Int,
    val maxRangeIndex: Int,
)

data class ViewportIndices(
    val displayedRangeIndex: Int,
    val firstDisplayedEntryIndex: Int,
)

sealed class NavigatorState {
    data object Initialised : NavigatorState()

    data class DataLoaded(
        val rangeLimits: RangeLimits,
    ) : NavigatorState()

    sealed class ViewportActive : NavigatorState() {
        abstract val rangeLimits: RangeLimits
        abstract val viewportIndices: ViewportIndices

        data class InitialDisplay(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()

        data class Navigating(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()
    }
}

class ChartNavigator(
    private val chart: CombinedChart,
) {
    private val barAnimator = BarAnimator(chart)

    private var _state: MutableStateFlow<NavigatorState> = MutableStateFlow(NavigatorState.Initialised)
    val state = _state.asStateFlow()

    // Must be called when chart data is loaded
    fun invalidate() {
        val range = chart.visibleXRange.roundToInt()
        val maxRangeIndex = (chart.data.maxEntryCountSet.entryCount / range) - INDEX_OFFSET
        _state.value = NavigatorState.DataLoaded(RangeLimits(range, maxRangeIndex))
    }

    // Works only when data is loaded. Must be called to show first range
    fun displayRange(rangeIndex: Int) {
        when (state.value) {
            is NavigatorState.Initialised -> return
            is NavigatorState.DataLoaded, is NavigatorState.ViewportActive -> {
                handleRangeChange(rangeIndex)
            }
        }
    }

    // works only when viewport is initialised meaning first range is displayed. Must be called to shift range
    // TODO delegate logic to another class
    fun navigateRange(
        direction: NavigationDirection,
        shiftRanges: Int = DEFAULT_RANGE_SHIFT,
    ) {
        when (val currentState = state.value) {
            is NavigatorState.Initialised, is NavigatorState.DataLoaded -> return
            is NavigatorState.ViewportActive ->
                handleViewportNavigation(
                    currentState,
                    direction,
                    shiftRanges,
                )
        }
    }

    fun resetToInitial() {
        _state.value = NavigatorState.Initialised
    }

    private fun handleViewportNavigation(
        currentState: NavigatorState.ViewportActive,
        direction: NavigationDirection,
        shiftRanges: Int,
    ) {
        val range = currentState.rangeLimits.chartRange
        val maxRangeIndex = currentState.rangeLimits.maxRangeIndex
        val minEntryIndex = currentState.viewportIndices.firstDisplayedEntryIndex
        val rangeIndex = currentState.viewportIndices.displayedRangeIndex

        when (direction) {
            NavigationDirection.PREVIOUS ->
                handlePreviousNavigation(
                    currentState,
                    rangeIndex,
                    minEntryIndex,
                    range,
                    shiftRanges,
                )

            NavigationDirection.NEXT ->
                handleNextNavigation(
                    currentState,
                    rangeIndex,
                    minEntryIndex,
                    range,
                    shiftRanges,
                    maxRangeIndex,
                )
        }
    }

    private fun handlePreviousNavigation(
        currentState: NavigatorState.ViewportActive,
        displayedRangeIndex: Int,
        displayedFirstEntryIndex: Int,
        range: Int,
        shiftRanges: Int,
    ) {
        if (displayedRangeIndex > FIRST_INDEX) {
            barAnimator.moveBars(displayedFirstEntryIndex, -range * shiftRanges)
            updateIndicesByShift(currentState, shiftRanges)
        }
    }

    private fun handleNextNavigation(
        currentState: NavigatorState.ViewportActive,
        displayedRangeIndex: Int,
        displayedFirstEntryIndex: Int,
        range: Int,
        shiftRanges: Int,
        maxRangeIndex: Int,
    ) {
        if (displayedRangeIndex < maxRangeIndex) {
            barAnimator.moveBars(displayedFirstEntryIndex, range * shiftRanges)
            updateIndicesByShift(currentState, shiftRanges)
        }
    }

    private fun handleRangeChange(rangeIndex: Int) {
        val rangeLimits =
            when (val currentState = state.value) {
                is NavigatorState.DataLoaded -> currentState.rangeLimits
                is NavigatorState.ViewportActive -> currentState.rangeLimits
                else -> return
            }

        val firstIndexToBeDisplayed = rangeIndex * rangeLimits.chartRange
        chart.moveViewToX(firstIndexToBeDisplayed - 0.5f)
        _state.value =
            when (state.value) {
                is NavigatorState.DataLoaded ->
                    NavigatorState.ViewportActive.InitialDisplay(
                        rangeLimits,
                        ViewportIndices(rangeIndex, firstIndexToBeDisplayed),
                    )

                is NavigatorState.ViewportActive ->
                    NavigatorState.ViewportActive.Navigating(
                        rangeLimits,
                        ViewportIndices(rangeIndex, firstIndexToBeDisplayed),
                    )

                else -> {
                    Log.d("My stack: ChartNavigator", "Wrong state: ${state.value}")
                    return
                }
            }
    }

    private fun updateIndicesByShift(
        currentState: NavigatorState.ViewportActive,
        shiftRanges: Int,
    ) {
        val rangeLimits = currentState.rangeLimits
        val indices = currentState.viewportIndices

        val range = rangeLimits.chartRange
        val maxRangeIndex = rangeLimits.maxRangeIndex

        val currentRangeIndex = indices.displayedRangeIndex

        val newRangeIndex =
            if (currentRangeIndex + shiftRanges < FIRST_INDEX) {
                FIRST_INDEX
            } else if (currentRangeIndex + shiftRanges > maxRangeIndex) {
                maxRangeIndex
            } else {
                currentRangeIndex + shiftRanges
            }

        _state.value =
            NavigatorState.ViewportActive.Navigating(rangeLimits, ViewportIndices(newRangeIndex, newRangeIndex * range))
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
