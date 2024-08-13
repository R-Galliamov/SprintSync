package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter

import com.github.mikephil.charting.formatter.ValueFormatter

abstract class SelectiveYAxisValueFormatter : ValueFormatter() {
    abstract fun selectYAxisValue(value: Float?)
}
