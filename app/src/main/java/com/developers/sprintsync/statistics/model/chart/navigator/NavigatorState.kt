package com.developers.sprintsync.statistics.model.chart.navigator

sealed class NavigatorState {
    data object Initialised : NavigatorState()

    data class DataLoaded(
        val rangeLimits: RangeLimits,
    ) : NavigatorState()

    sealed class ViewportActive : NavigatorState() {
        abstract val rangeLimits: RangeLimits
        abstract val viewportIndices: ViewportIndices
        val rangePosition: RangePosition
            get() = calculateRangePosition(rangeLimits.maxRangeIndex, viewportIndices.displayedRangeIndex)

        data class InitialDisplay(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()

        data class Navigating(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()

        data class DataReloaded(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()

        private fun calculateRangePosition(
            maxRangeIndex: Int,
            displayedRangeIndex: Int,
        ): RangePosition =
            when {
                (maxRangeIndex == FIRST_INDEX) -> {
                    RangePosition.ONLY
                }

                displayedRangeIndex ==
                    FIRST_INDEX -> {
                    RangePosition.FIRST
                }

                displayedRangeIndex ==
                    maxRangeIndex -> {
                    RangePosition.LAST
                }

                else -> {
                    RangePosition.MIDDLE
                }
            }
    }

    companion object {
        private const val FIRST_INDEX = 0
    }
}
