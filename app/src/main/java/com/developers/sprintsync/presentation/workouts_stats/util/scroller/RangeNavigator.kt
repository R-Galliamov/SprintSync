package com.developers.sprintsync.presentation.workouts_stats.util.scroller

import com.developers.sprintsync.databinding.ProgressChartNavigatorBinding
import com.developers.sprintsync.presentation.workouts_stats.chart.navigator.RangePosition

class RangeNavigator {
    fun updateNavigatingButtonsUI(
        navigator: ProgressChartNavigatorBinding,
        rangePosition: RangePosition,
    ) {
        when (rangePosition) {
            RangePosition.NOT_INITIALIZED -> {
                navigator.apply {
                    btnPreviousRange.isEnabled = false
                    btnNextRange.isEnabled = false
                }
            }

            RangePosition.FIRST -> {
                navigator.apply {
                    btnPreviousRange.isEnabled = false
                    btnNextRange.isEnabled = true
                }
            }

            RangePosition.MIDDLE -> {
                navigator.apply {
                    btnPreviousRange.isEnabled = true
                    btnNextRange.isEnabled = true
                }
            }

            RangePosition.LAST -> {
                navigator.apply {
                    btnPreviousRange.isEnabled = true
                    btnNextRange.isEnabled = false
                }
            }

            RangePosition.ONLY -> {
                navigator.apply {
                    btnPreviousRange.isEnabled = false
                    btnNextRange.isEnabled = false
                }
            }
        }
    }
}
