package com.developers.sprintsync.user.model.chart.navigator

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

        data class DataReloaded(
            override val rangeLimits: RangeLimits,
            override val viewportIndices: ViewportIndices,
        ) : ViewportActive()
    }
}
