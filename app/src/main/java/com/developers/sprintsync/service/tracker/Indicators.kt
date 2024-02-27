package com.developers.sprintsync.service.tracker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO create Mutable/Immutable interface
class Indicators
    @Inject
    constructor(
        private val routeTracker: RouteTracker,
    ) {
        private var _kCalories = MutableStateFlow(0)
        val kCalories: StateFlow<Int> get() = _kCalories.asStateFlow()

        private var _pace = MutableStateFlow(0f)
        val pace: StateFlow<Float> get() = _pace.asStateFlow()

        private var _distanceInMeters = MutableStateFlow(0)
        val distanceInMeters: StateFlow<Int> get() = _distanceInMeters.asStateFlow()

        fun timeInMillisFlow(): Flow<Long> = routeTracker.timeInMillisFlow()

        fun initTimeUpdates(onUpdate: ((timeMillis: Long) -> Unit)) {
            CoroutineScope(Dispatchers.IO).launch {
                timeInMillisFlow().collect { time ->
                    onUpdate(time)
                }
            }
        }

        fun initDistanceUpdates(onUpdate: ((distanceMeters: Int) -> Unit)) {
            CoroutineScope(Dispatchers.IO).launch {
                distanceInMeters.collect { distance ->
                    onUpdate(distance)
                }
            }
        }

        fun updatePace(pace: Float) {
            _pace.value = pace
        }

        fun updateDistance(distanceInMeters: Int) {
            _distanceInMeters.value += distanceInMeters
        }

        fun updateKCalories(kCalories: Int) {
            _kCalories.value += kCalories
        }
    }
