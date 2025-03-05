package com.developers.sprintsync.tracking_session.presentation.tracking.util.state_handler.event

import com.developers.sprintsync.core.components.track.data.data_source.preparer.StationarySegmentMerger
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.tracking.data.model.Latitude
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.Longitude
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StationarySegmentMergerTest {

    private val merger = StationarySegmentMerger()

    // Base locations for simplicity
    private val locA = LocationModel(Latitude(1.0), Longitude(2.0))
    private val locB = LocationModel(Latitude(3.0), Longitude(4.0))
    private val locC = LocationModel(Latitude(5.0), Longitude(6.0))

    @Test
    fun emptyListReturnsEmptyList() {
        val input = emptyList<Segment>()
        val result = merger.mergeStationarySegments(input)
        assertEquals(emptyList<Segment>(), result)
    }

    @Test
    fun singleActiveSegmentPreservesSegmentWithIdZero() {
        val input = listOf(
            Segment.Active(99, locA, 0, locB, 1000, 1000, 100f, 10f, 50f)
        )
        val expected = listOf(
            Segment.Active(0, locA, 0, locB, 1000, 1000, 100f, 10f, 50f)
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun singleStationarySegmentPreservesSegmentWithIdZero() {
        val input = listOf(
            Segment.Stationary(99, locA, 0, 1000, 1000)
        )
        val expected = listOf(
            Segment.Stationary(0, locA, 0, 1000, 1000)
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun twoConsecutiveStationarySegmentsMergesIntoOneWithIdZero() {
        val input = listOf(
            Segment.Stationary(1, locA, 0, 1000, 1000),
            Segment.Stationary(2, locA, 1000, 2000, 1000)
        )
        val expected = listOf(
            Segment.Stationary(0, locA, 0, 3000, 2000) // duration: 1000+1000, endTime: 1000+2000
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun activeFollowedByStationaryPreservesBothWithIncrementalIds() {
        val input = listOf(
            Segment.Active(1, locA, 0, locB, 1000, 1000, 100f, 10f, 50f),
            Segment.Stationary(2, locB, 1000, 2000, 1000)
        )
        val expected = listOf(
            Segment.Active(0, locA, 0, locB, 1000, 1000, 100f, 10f, 50f),
            Segment.Stationary(1, locB, 1000, 2000, 1000)
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun mixedSegmentsWithConsecutiveStationaryMergesCorrectlyWithIds() {
        val input = listOf(
            Segment.Active(1, locA, 0, locB, 1000, 1000, 100f, 10f, 50f),
            Segment.Stationary(2, locB, 1000, 2000, 1000),
            Segment.Stationary(3, locB, 2000, 3000, 1000),
            Segment.Active(4, locB, 3000, locC, 4000, 1000, 100f, 10f, 50f)
        )
        val expected = listOf(
            Segment.Active(0, locA, 0, locB, 1000, 1000, 100f, 10f, 50f),
            Segment.Stationary(1, locB, 1000, 5000, 2000), // Merged: 1000+1000, 2000+3000
            Segment.Active(2, locB, 3000, locC, 4000, 1000, 100f, 10f, 50f)
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun threeConsecutiveStationarySegmentsMergesIntoOneWithIdZero() {
        val input = listOf(
            Segment.Stationary(1, locA, 0, 1000, 1000),
            Segment.Stationary(2, locA, 1000, 2000, 1000),
            Segment.Stationary(3, locA, 2000, 3000, 1000)
        )
        val expected = listOf(
            Segment.Stationary(0, locA, 0, 6000, 3000) // 1000+1000+1000, 3000+3000
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(expected, result)
    }

    @Test
    fun multipleSegmentsEnsuresIncrementalIdsInOrder() {
        val input = listOf(
            Segment.Active(1, locA, 0, locB, 1000, 1000, 100f, 10f, 50f),
            Segment.Stationary(2, locB, 1000, 2000, 1000),
            Segment.Stationary(3, locB, 2000, 3000, 1000),
            Segment.Active(4, locB, 3000, locC, 4000, 1000, 100f, 10f, 50f)
        )
        val result = merger.mergeStationarySegments(input)
        assertEquals(3, result.size) // Expect 3 segments after merging
        assertEquals(0L, result[0].id) // Active
        assertEquals(1L, result[1].id) // Merged Stationary
        assertEquals(2L, result[2].id) // Active
    }
}