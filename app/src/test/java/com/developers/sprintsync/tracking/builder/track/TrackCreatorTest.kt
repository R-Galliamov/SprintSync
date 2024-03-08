package com.developers.sprintsync.tracking.builder.track

import com.developers.sprintsync.tracking.model.TrackSegment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TrackCreatorTest {
    @Test
    fun createTrackData_withValidSegmentCreatesTrack() {
        // Arrange
        val mockTrackSegment = mock(TrackSegment::class.java)
        Mockito.`when`(mockTrackSegment.startTime).thenReturn(0L)
        Mockito.`when`(mockTrackSegment.endTime).thenReturn(1000L)
        Mockito.`when`(mockTrackSegment.durationMillis).thenReturn(1000L)
        Mockito.`when`(mockTrackSegment.distanceMeters).thenReturn(100)
        Mockito.`when`(mockTrackSegment.pace).thenReturn(6.0f)
        Mockito.`when`(mockTrackSegment.burnedKCalories).thenReturn(100)
        val trackCreator = TrackCreator()

        // Act
        val track = trackCreator.createTrackData(mockTrackSegment)

        // Assert
        assertEquals(0, track.id)
        assertEquals(1000L, track.durationMillis)
        assertEquals(100, track.distanceMeters)
        assertEquals(6.0f, track.avgPace)
        assertEquals(6.0f, track.maxPace)
        assertEquals(100, track.calories)
        assertEquals(1, track.segments.size)
        assertEquals(mockTrackSegment, track.segments[0])
    }
}
