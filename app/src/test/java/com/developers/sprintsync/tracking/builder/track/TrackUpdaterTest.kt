package com.developers.sprintsync.tracking.builder.track

import com.developers.sprintsync.tracking.builder.segment.SegmentGenerator
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackSegment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TrackUpdaterTest {
    @Mock
    private lateinit var creator: TrackCreator

    @Mock
    private lateinit var calculator: TrackStatCalculator

    @Mock
    private lateinit var mockGenerator: SegmentGenerator

    private lateinit var updater: TrackUpdater

    @Before
    fun setUp() {
        updater = TrackUpdater(creator, calculator, mockGenerator)
    }

    @Test
    fun getTrack_returnsEmptyTrackWhenCurrentTrackIsEmpty() {
        // Arrange
        val location = mock(LocationModel::class.java)
        val timeMillis = 1000L
        val expectedTrack = Track.EMPTY_TRACK_DATA

        // Act
        val track = updater.getTrack(location, timeMillis)

        // Assert
        assertEquals(track, expectedTrack)
    }

    @Test
    fun getTrack_returnsCreatedTrackWhenSegmentGeneratorReturnsSegmentForTheFirstTime() {
        // Arrange
        val startLocation = mock(LocationModel::class.java)
        val startTimeMillis = 0L
        val endLocation = mock(LocationModel::class.java)
        val endTimeMillis = 100L
        val expectedSegment = mock(TrackSegment::class.java)
        val expectedTrack = mock(Track::class.java)
        `when`(mockGenerator.nextSegmentOrNull(startLocation, startTimeMillis)).thenReturn(null)
        `when`(mockGenerator.nextSegmentOrNull(endLocation, endTimeMillis)).thenReturn(
            expectedSegment,
        )
        `when`(creator.createTrackData(expectedSegment)).thenReturn(expectedTrack)

        // Act
        updater.getTrack(startLocation, startTimeMillis)
        val track = updater.getTrack(endLocation, endTimeMillis)

        // Assert
        assertEquals(expectedTrack, track)
    }
}
