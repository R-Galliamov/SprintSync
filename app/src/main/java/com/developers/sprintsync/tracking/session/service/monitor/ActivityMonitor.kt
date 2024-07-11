package com.developers.sprintsync.tracking.session.service.monitor

import com.developers.sprintsync.tracking.session.model.indicator.PaceAnalysisResult
import com.developers.sprintsync.tracking.session.model.session.UserActivityState
import com.developers.sprintsync.tracking.session.model.track.Segment
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

        fun isStopped(): Boolean = userActivityState.value is UserActivityState.HasStopped

        fun stopMonitoringInactivity() {
            debouncer.cancel()
        }

        private fun updateState(userActivityState: UserActivityState) {
            _userActivityState.value = userActivityState
        }

        private fun isPaceSlowedDown(segments: List<Segment>): Boolean =
            (PaceAnalyzer.analyzePaceChange(segments) == PaceAnalysisResult.PaceSlowedDown)
    }
