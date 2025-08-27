package com.developers.sprintsync.fakes

import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.domain.track.model.LocationModel
import java.util.ArrayDeque

/**
 * Test fake for DistanceCalculator.
 * Priority: per-pair override -> scripted queue -> defaultReturn.
 */
class FakeDistanceCalculator : DistanceCalculator {

    data class Call(val l1: LocationModel, val l2: LocationModel)

    /** Captured calls for verification. */
    val calls: MutableList<Call> = mutableListOf()

    /** Default distance if no override or scripted value exists. */
    var defaultReturn: Float = 0f

    /** FIFO scripted returns. Useful for sequential assertions. */
    private val scripted: ArrayDeque<Float> = ArrayDeque()

    /** Exact (ordered) per-pair overrides. */
    private val perPair: MutableMap<Pair<LocationModel, LocationModel>, Float> = mutableMapOf()

    /** Set an override for a specific ordered pair. */
    fun setDistance(l1: LocationModel, l2: LocationModel, meters: Float) {
        perPair[l1 to l2] = meters
    }

    /** Set symmetric overrides for both directions. */
    fun setSymmetricDistance(a: LocationModel, b: LocationModel, meters: Float) {
        perPair[a to b] = meters
        perPair[b to a] = meters
    }

    /** Enqueue scripted distances. Returned in insertion order. */
    fun enqueue(vararg meters: Float) {
        scripted.addAll(meters.toList())
    }

    /** Helpers for tests. */
    fun clear() {
        calls.clear(); scripted.clear(); perPair.clear()
    }

    fun lastCall(): Call? = calls.lastOrNull()
    fun callCount(): Int = calls.size

    override fun distM(l1: LocationModel, l2: LocationModel): Float {
        calls += Call(l1, l2)
        perPair[l1 to l2]?.let { return it }
        if (scripted.isNotEmpty()) return scripted.removeFirst()
        return defaultReturn
    }
}