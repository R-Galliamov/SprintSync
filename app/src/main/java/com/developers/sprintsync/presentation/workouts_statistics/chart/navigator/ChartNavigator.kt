package com.developers.sprintsync.presentation.workouts_statistics.chart.navigator

import android.util.Log
import com.github.mikephil.charting.charts.CombinedChart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ChartNavigator(
    private val chart: CombinedChart,
) {
    private val chartScrollAnimator = ChartScrollAnimator(chart)

    private var _state: MutableStateFlow<NavigatorState> = MutableStateFlow(NavigatorState.Initialised)
    val state = _state.asStateFlow()

    // Must be called when chart data is loaded
    fun invalidate() {
        val range = chart.visibleXRange.roundToInt()
        val maxRangeIndex = calculateMaxRangeIndex(range)
        val rangeLimits = RangeLimits(range, maxRangeIndex)
        _state.update {
            when (val currentState = state.value) {
                is NavigatorState.Initialised -> NavigatorState.DataLoaded(rangeLimits)
                is NavigatorState.DataLoaded -> {
                    if (currentState.rangeLimits != rangeLimits) {
                        NavigatorState.DataLoaded(rangeLimits)
                    } else {
                        currentState
                    }
                }

                is NavigatorState.ViewportActive -> {
                    NavigatorState.ViewportActive.DataReloaded(
                        rangeLimits,
                        currentState.viewportIndices,
                    )
                }
            }
        }
    }

    fun commitDataReload() {
        when (val currentState = state.value) {
            is NavigatorState.Initialised -> return
            is NavigatorState.DataLoaded -> return
            is NavigatorState.ViewportActive.InitialDisplay -> return
            is NavigatorState.ViewportActive.Navigating -> return
            is NavigatorState.ViewportActive.DataReloaded -> {
                _state.update {
                    NavigatorState.ViewportActive.Navigating(
                        currentState.rangeLimits,
                        currentState.viewportIndices,
                    )
                }
            }
        }
    }

    // Works only when data is loaded. Must be called to show first range
    fun setDisplayedRange(rangeIndex: Int) {
        when (state.value) {
            is NavigatorState.Initialised -> return
            is NavigatorState.DataLoaded, is NavigatorState.ViewportActive -> {
                handleRangeChange(rangeIndex)
            }
        }
    }

    // works only when viewport is initialised meaning first range is displayed. Must be called to shift range
    // TODO delegate logic to another class
    fun shiftViewPortRange(
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

    fun resetNavigationState() {
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
            chartScrollAnimator.animateBarScroll(displayedFirstEntryIndex, range * -shiftRanges)
            updateIndicesByShift(currentState, -shiftRanges)
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
            chartScrollAnimator.animateBarScroll(displayedFirstEntryIndex, range * shiftRanges)
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

        val coercedRangeIndex = rangeIndex.coerceIn(FIRST_INDEX, rangeLimits.maxRangeIndex)

        val firstIndexToBeDisplayed = coercedRangeIndex * rangeLimits.chartRange

        CoroutineScope(Dispatchers.Main).launch {
            chart.moveViewToX(firstIndexToBeDisplayed - 0.5f) // TODO add entry index converter to x position
            _state.update {
                when (state.value) {
                    is NavigatorState.DataLoaded ->
                        NavigatorState.ViewportActive.InitialDisplay(
                            rangeLimits,
                            ViewportIndices(coercedRangeIndex, firstIndexToBeDisplayed),
                        )

                    is NavigatorState.ViewportActive ->
                        NavigatorState.ViewportActive.Navigating(
                            rangeLimits,
                            ViewportIndices(coercedRangeIndex, firstIndexToBeDisplayed),
                        )

                    else -> {
                        Log.d("My stack: ChartNavigator", "Wrong state: ${state.value}")
                        return@launch
                    }
                }
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
        if (maxRangeIndex < FIRST_INDEX) return

        val currentRangeIndex = indices.displayedRangeIndex

        val coercedRangeIndex = (currentRangeIndex + shiftRanges).coerceIn(FIRST_INDEX, maxRangeIndex)

        _state.value =
            NavigatorState.ViewportActive.Navigating(
                rangeLimits,
                ViewportIndices(coercedRangeIndex, coercedRangeIndex * range),
            )
    }

    private fun calculateMaxRangeIndex(range: Int): Int {
        Log.d(TAG, "calculateMaxRangeIndex: state = ${state.value}")
        val entryCount = chart.data.maxEntryCountSet.entryCount
        return (entryCount / range) - INDEX_OFFSET
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
