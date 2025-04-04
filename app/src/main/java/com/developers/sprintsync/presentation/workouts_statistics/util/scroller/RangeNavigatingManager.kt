package com.developers.sprintsync.presentation.workouts_statistics.util.scroller

import com.developers.sprintsync.databinding.ProgressChartNavigatorBinding
import com.developers.sprintsync.presentation.workouts_statistics.chart.navigator.RangePosition

class RangeNavigatingManager {
    fun updateNavigatingButtonsUI(
        navigator: ProgressChartNavigatorBinding,
        rangePosition: RangePosition,
    ) {
        when (rangePosition) {
            RangePosition.NOT_INITIALIZED -> {
                navigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = false
                }
            }

            RangePosition.FIRST -> {
                navigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = true
                }
            }

            RangePosition.MIDDLE -> {
                navigator.apply {
                    btPreviousRange.isEnabled = true
                    btNextRange.isEnabled = true
                }
            }

            RangePosition.LAST -> {
                navigator.apply {
                    btPreviousRange.isEnabled = true
                    btNextRange.isEnabled = false
                }
            }

            RangePosition.ONLY -> {
                navigator.apply {
                    btPreviousRange.isEnabled = false
                    btNextRange.isEnabled = false
                }
            }
        }
    }
}
