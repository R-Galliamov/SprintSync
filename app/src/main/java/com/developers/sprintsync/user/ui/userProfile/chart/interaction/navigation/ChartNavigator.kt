package com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation

import android.util.Log
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.animation.BarAnimator
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

/*
enum class NavigatorState {
    INITIAL_LOAD,
    DATA_LOADED,
}

sealed class NavigatorEvent {
    data object DataLoading : NavigatorEvent()

    data object DataUpdate : NavigatorEvent()
}

abstract class ChartNavigatorStateMachine {
    private var state: NavigatorState = NavigatorState.INITIAL_LOAD

    fun handleEvent(event: NavigatorEvent) {
        when (state) {
            NavigatorState.INITIAL_LOAD -> handleInitialLoad(event)
            NavigatorState.DATA_LOADED -> handleDataLoaded(event)
        }
    }

    abstract fun handleDataLoadingWhenInitialLoad()

    private fun handleInitialLoad(event: NavigatorEvent) {
        when (event) {
            is NavigatorEvent.DataLoading -> {
                // Handle initial data loading
                // display range
                // scale up maximum without animation
                handleDataLoadingWhenInitialLoad()
                state = NavigatorState.DATA_LOADED
            }

            is NavigatorEvent.DataUpdate -> {
                // Ignore updates in the initial load state
            }
        }
    }

    private fun handleDataLoaded(event: NavigatorEvent) {
        when (event) {
            is NavigatorEvent.DataLoading -> {
                // Handle data loading (e.g., reset navigator)
                // if configuration changed for example
            }

            is NavigatorEvent.DataUpdate -> {
                // Handle data update (e.g., keep current view)
            }
        }
    }
}

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
    private val stateMachine: ChartNavigatorStateMachine,
) {
    private val barAnimator = BarAnimator(chart)

    private val _rangeLimitsState: MutableStateFlow<RangeLimitsState> = MutableStateFlow(RangeLimitsState.Undefined)
    val rangeLimits = _rangeLimitsState.asStateFlow()

    private val _indicesState: MutableStateFlow<ViewportIndicesState> = MutableStateFlow(ViewportIndicesState.Undefined)
    val indices = _indicesState.asStateFlow()

    fun invalidate() {
        val range = chart.visibleXRange.roundToInt()
        val maxRangeIndex = (chart.data.maxEntryCountSet.entryCount / range) - INDEX_OFFSET

        _rangeLimitsState.value = RangeLimitsState.Loaded(range, maxRangeIndex)

        if (indices.value is ViewportIndicesState.Undefined) {
            _indicesState.value = ViewportIndicesStateFactory.createDefault()
        }

        stateMachine.handleEvent(NavigatorEvent.DataLoading)
    }

    // TODO write index converter to x position

    /**
     * Displays a range of data in the chart.
     * Pass [Int.MAX_VALUE] to display last range.
     * Pass [Int.MIN_VALUE] to display first range.
     */
    fun displayRange(rangeIndex: Int) {
        stateMachine.handleEvent(NavigatorEvent.DataUpdate)

        val rangeLimits = rangeLimits.value
        val indices = indices.value

        if (indices is ViewportIndicesState.Undefined) {
            _indicesState.value = ViewportIndicesStateFactory.createDefault()
        }

        executeIfDataLoaded(rangeLimits, indices) { loadedLimits, loadedIndices ->
            Log.d("My stack: ChartNavigator", "displayRange: $rangeIndex")
            val currentRangeIndex = loadedIndices.displayedRangeIndex

            val shiftRanges = rangeIndex - currentRangeIndex
            updateIndicesByShift(shiftRanges)

            val updatedIndices = _indicesState.value as ViewportIndicesState.Loaded

            val firstIndexToBeDisplayed = updatedIndices.firstDisplayedEntryIndex
            chart.moveViewToX(firstIndexToBeDisplayed - 0.5f)
        }
    }

    fun navigateRange(
        direction: NavigationDirection,
        shiftRanges: Int = DEFAULT_RANGE_SHIFT,
    ) {
        val indices = indices.value
        val limits = rangeLimits.value

        executeIfDataLoaded(limits, indices) { loadedLimits, loadedIndices ->
            val range = loadedLimits.chartRange
            val maxRangeIndex = loadedLimits.maxRangeIndex

            val minEntryIndex = loadedIndices.firstDisplayedEntryIndex
            val rangeIndex = loadedIndices.displayedRangeIndex

            when (direction) {
                NavigationDirection.PREVIOUS -> {
                    if (minEntryIndex > FIRST_INDEX) {
                        barAnimator.moveBars(minEntryIndex, -range * shiftRanges)
                        updateIndicesByShift(-shiftRanges)
                    }
                }

                NavigationDirection.NEXT -> {
                    if (rangeIndex < maxRangeIndex) {
                        barAnimator.moveBars(minEntryIndex, range * shiftRanges)
                        updateIndicesByShift(shiftRanges)
                    }
                }
            }
        }
    }

    fun executeIfDataLoaded(
        rangeLimits: RangeLimitsState,
        indices: ViewportIndicesState,
        action: (validRangeLimits: RangeLimitsState.Loaded, validIndices: ViewportIndicesState.Loaded) -> Unit,
    ) {
        when (rangeLimits) {
            is RangeLimitsState.Loaded ->
                when (indices) {
                    is ViewportIndicesState.Loaded -> {
                        action.invoke(rangeLimits, indices)
                    }

                    ViewportIndicesState.Undefined -> return
                }

            is RangeLimitsState.Undefined -> return
        }
    }

    private fun updateIndicesByShift(shiftRanges: Int) {
        val indices = indices.value
        val limits = rangeLimits.value

        executeIfDataLoaded(limits, indices) { loadedLimits, loadedIndices ->
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

            val updatedIndices = ViewportIndicesStateFactory.create(rangeIndex = newRangeIndex, chartRange = range)
            _indicesState.value = updatedIndices
        }
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

 */
