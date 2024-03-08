package com.developers.sprintsync.tracking.builder.segment

import com.developers.sprintsync.tracking.mapper.model.TrackSegmentMapper
import com.developers.sprintsync.tracking.model.GeoTimePair
import com.developers.sprintsync.tracking.model.Latitude
import com.developers.sprintsync.tracking.model.LocationModel
import com.developers.sprintsync.tracking.model.Longitude
import com.developers.sprintsync.tracking.model.TrackSegment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SegmentGeneratorTest {
    @Mock
    private lateinit var mapper: TrackSegmentMapper

    private lateinit var segmentGenerator: SegmentGenerator

    @Before
    fun setUp() {
        segmentGenerator = SegmentGenerator(mapper)
    }

    @Test
    fun nextSegmentOrNull_ReturnsNullWhenThereIsNoPreviousPair() {
        // Arrange
        val location = LocationModel(Latitude(0.0), Longitude(0.0))
        val timeMillis = 0L

        // Act
        val result = segmentGenerator.nextSegmentOrNull(location, timeMillis)

        // Assert
        assertNull(result)
    }

    @Test
    fun nextSegmentOrNull_ReturnsATrackSegmentWhenPreviousPairIsNotNull() {
        // Arrange
        val startLocation = mock(LocationModel::class.java)
        val startTimeMillis = 0L
        val endLocation = mock(LocationModel::class.java)
        val endTimeMillis = 100L
        val expectedSegment = mock(TrackSegment::class.java)
        segmentGenerator.nextSegmentOrNull(startLocation, startTimeMillis)

        Mockito.`when`(
            mapper.buildTrackSegment(
                startData = GeoTimePair(startLocation, startTimeMillis),
                endData = GeoTimePair(endLocation, endTimeMillis),
            ),
        ).thenReturn(expectedSegment)

        // Act
        val result = segmentGenerator.nextSegmentOrNull(endLocation, endTimeMillis)

        // Assert
        assertNotNull(result)
        assertEquals(expectedSegment, result)
    }

    @Test
    fun reset() {
        // Arrange
        val location = mock(LocationModel::class.java)
        val timeMillis = 1000L
        segmentGenerator.nextSegmentOrNull(location, timeMillis)

        // Act
        segmentGenerator.reset()

        // Assert
        assertNull(segmentGenerator.nextSegmentOrNull(location, timeMillis))
    }
}
