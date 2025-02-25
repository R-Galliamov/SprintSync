package com.developers.sprintsync.tracking_session.presentation.session_summary

import com.developers.sprintsync.core.components.track.presentation.model.UiTrack
import com.developers.sprintsync.core.presentation.view.pace_chart.model.PaceChartData

sealed class SessionSummaryState {
    data object Loading : SessionSummaryState()

    data class Success(
        val track: UiTrack,
        val paceChartData: PaceChartData,
    ) : SessionSummaryState()

    data object Error : SessionSummaryState()
}