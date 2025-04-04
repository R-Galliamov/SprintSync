package com.developers.sprintsync.tracking.activity_monitoring

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.tracking.activity_monitoring.pace_analyzer.PaceAnalysisResult
import com.developers.sprintsync.tracking.activity_monitoring.pace_analyzer.PaceAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ActivityMonitor
    @Inject
    constructor(
        private val debouncer: Debouncer,
    ) {
        private val _userActivityState: MutableStateFlow<UserActivityState> =
            MutableStateFlow(UserActivityState.Running)
        val userActivityState = _userActivityState.asStateFlow()

        fun updateState(segments: List<Segment>) {
            if (isPaceSlowedDown(segments)) {
                updateState(UserActivityState.Running)
            } else {
                updateState(UserActivityState.HasSlowedDown)
            }
        }

        fun startMonitoringInactivity() {
            debouncer.debounce {
                updateState(UserActivityState.HasStopped)
            }
        }

        fun userHasStopped(): Boolean = userActivityState.value is UserActivityState.HasStopped

        fun stopMonitoringInactivity() {
            debouncer.cancel()
        }

        private fun updateState(userActivityState: UserActivityState) {
            _userActivityState.value = userActivityState
        }

        private fun isPaceSlowedDown(segments: List<Segment>): Boolean =
            (PaceAnalyzer.analyzePaceChange(segments) == PaceAnalysisResult.PaceSlowedDown)
    }
