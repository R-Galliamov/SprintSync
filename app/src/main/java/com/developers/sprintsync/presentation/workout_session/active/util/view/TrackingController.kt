package com.developers.sprintsync.presentation.workout_session.active.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.ViewTrackingControllerBinding

class TrackingController (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val binding = ViewTrackingControllerBinding.inflate(LayoutInflater.from(context), this, true)

    sealed class State {
        data object Initialized : State()

        data object Active : State()

        data object Paused : State()

        val isTracking: Boolean get() = this is Active
        val isInitialized: Boolean get() = this is Initialized
        val isPaused: Boolean get() = this is Paused

        fun controllerAction(onStart: () -> Unit, onPause: () -> Unit): () -> Unit = when (this) {
            Initialized, Paused -> onStart
            Active -> onPause
        }

        fun backgroundRes(): Int = when (this) {
            Active -> R.drawable.bt_circle_thirdhly
            else -> R.drawable.bt_circle_secondary
        }

        fun iconRes(): Int = when (this) {
            Active -> R.drawable.ic_pause_48dp
            else -> R.drawable.ic_start_48dp
        }

        fun textRes(): Int = when (this) {
            Initialized -> R.string.action_start
            else -> View.NO_ID
        }
    }

    interface OnInteractionListener {
        fun onStart()
        fun onPause()
        fun onFinish()
    }

    private var currentState: State = State.Initialized

    fun setOnInteractionListener(interactions: OnInteractionListener) {
        binding.btnActivityController.setOnClickListener {
            currentState.controllerAction(interactions::onStart, interactions::onPause).invoke()
        }
        binding.btnFinish.setOnClickListener { interactions.onFinish() }
        updateState(currentState)
    }

    fun updateState(state: State) {
        currentState = state
        binding.apply {
            tvTrackingController.visibility = state.isInitialized.toVisibility()
            imTrackingController.visibility = (!state.isInitialized).toVisibility()
            btnFinish.visibility = (state.isTracking || state.isPaused).toVisibility()

            btnActivityController.setBackgroundResource(state.backgroundRes())
            imTrackingController.setBackgroundResource(state.iconRes())

            if (state.textRes() != View.NO_ID) {
                tvTrackingController.setText(state.textRes())
            }
        }
    }

    private fun Boolean.toVisibility(): Int =
        if (this) View.VISIBLE else View.GONE
}