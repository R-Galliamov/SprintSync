package com.developers.sprintsync.tracking_session.presentation.tracking.util.tracking_panel

import android.view.View
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.ViewTrackingControllerBinding

class TrackingPanelController(
    private val panelView: ViewTrackingControllerBinding,
    private val onStart: (() -> Unit),
    private val onPause: (() -> Unit),
    private val onFinish: (() -> Unit),
) {
    private var currentState: TrackingPanelState = TrackingPanelState.Initialized

    init {
        panelView.btTrackingController.setOnClickListener { handleClick(currentState) }
        panelView.btFinish.setOnClickListener { onFinish() }
    }

    fun updateState(state: TrackingPanelState) {
        currentState = state
        updateUi(state)
    }

    private fun handleClick(state: TrackingPanelState): Unit =
        when (state) {
            TrackingPanelState.Initialized -> onStart()
            TrackingPanelState.Active -> onPause()
            TrackingPanelState.Paused -> onStart()
        }

    private fun updateUi(state: TrackingPanelState) {
        val isInitialized = state == TrackingPanelState.Initialized
        val isTracking = state == TrackingPanelState.Active
        val isPaused = state == TrackingPanelState.Paused

        panelView.apply {
            tvTrackingController.visibility = isVisible(isInitialized)
            imTrackingController.visibility = isVisible(isInitialized.not())
            btFinish.visibility = isVisible(isTracking || isPaused)

            btTrackingController.setBackgroundResource(getBackgroundForState(state))
            imTrackingController.setBackgroundResource(getIconForState(state))

            if (isInitialized) {
                tvTrackingController.text = panelView.root.context.getText(R.string.start)
            }
        }
    }

    private fun getBackgroundForState(state: TrackingPanelState): Int =
        when (state) {
            TrackingPanelState.Active -> R.drawable.bt_circle_thirdhly
            else -> R.drawable.bt_circle_secondary
        }

    private fun getIconForState(state: TrackingPanelState): Int =
        when (state) {
            TrackingPanelState.Active -> R.drawable.ic_pause_48dp
            else -> R.drawable.ic_start_48dp
        }

    private fun isVisible(condition: Boolean) = if (condition) View.VISIBLE else View.GONE
}
