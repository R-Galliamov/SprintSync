package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Segments
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineFormatter
import com.developers.sprintsync.tracking.data.model.Latitude
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.Longitude
import com.google.android.gms.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PolylineFormatterTest {
    private lateinit var formatter: PolylineFormatter

    @BeforeEach
    fun setUp() {
        formatter = PolylineFormatter()
    }

    @Test
    fun formatEmptySegmentsReturnsEmptyList() {
        val segments: Segments = emptyList()
        val result = formatter.format(segments)
        assertEquals(0, result.size)
    }

    @Test
    fun formatSingleActiveSegmentCreatesOnePolyline() {
        val segments: Segments =
            listOf(
                Segment.Active(
                    1L,
                    LocationModel(Latitude(40.7128), Longitude(-74.0060)),
                    0L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
            )
        val result = formatter.format(segments)
        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals(LatLng(40.7128, -74.0060), result[0][0])
        assertEquals(LatLng(40.7138, -74.0070), result[0][1])
    }

    @Test
    fun formatMultipleContinuousActiveSegmentsCreatesOnePolyline() {
        val segments: Segments =
            listOf(
                Segment.Active(
                    1L,
                    LocationModel(Latitude(40.7128), Longitude(-74.0060)),
                    0L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
                Segment.Active(
                    2L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    LocationModel(Latitude(40.7148), Longitude(-74.0080)),
                    7200000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
            )
        val result = formatter.format(segments)
        assertEquals(1, result.size)
        assertEquals(3, result[0].size)
        assertEquals(LatLng(40.7128, -74.0060), result[0][0])
        assertEquals(LatLng(40.7138, -74.0070), result[0][1])
        assertEquals(LatLng(40.7148, -74.0080), result[0][2])
    }

    @Test
    fun formatActiveSegmentsWithBreakCreatesMultiplePolylines() {
        val segments: Segments =
            listOf(
                Segment.Active(
                    1L,
                    LocationModel(Latitude(40.7128), Longitude(-74.0060)),
                    0L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
                Segment.Active(
                    2L,
                    LocationModel(Latitude(40.7140), Longitude(-74.0080)), // Different start
                    7200000L,
                    LocationModel(Latitude(40.7150), Longitude(-74.0090)),
                    10800000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
            )
        val result = formatter.format(segments)
        assertEquals(2, result.size)
        assertEquals(2, result[0].size)
        assertEquals(LatLng(40.7128, -74.0060), result[0][0])
        assertEquals(LatLng(40.7138, -74.0070), result[0][1])
        assertEquals(2, result[1].size)
        assertEquals(LatLng(40.7140, -74.0080), result[1][0])
        assertEquals(LatLng(40.7150, -74.0090), result[1][1])
    }

    @Test
    fun formatWithStationarySegmentCreatesNewPolyline() {
        val segments: Segments =
            listOf(
                Segment.Active(
                    1L,
                    LocationModel(Latitude(40.7128), Longitude(-74.0060)),
                    0L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
                Segment.Stationary(
                    2L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    7200000L,
                    3600000L,
                ),
                Segment.Active(
                    3L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    7200000L,
                    LocationModel(Latitude(40.7148), Longitude(-74.0080)),
                    10800000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
            )
        val result = formatter.format(segments)
        assertEquals(2, result.size)
        assertEquals(2, result[0].size)
        assertEquals(LatLng(40.7128, -74.0060), result[0][0])
        assertEquals(LatLng(40.7138, -74.0070), result[0][1])
        assertEquals(2, result[1].size)
        assertEquals(LatLng(40.7138, -74.0070), result[1][0])
        assertEquals(LatLng(40.7148, -74.0080), result[1][1])
    }

    @Test
    fun formatWithOnlyStationarySegmentsReturnsEmptyList() {
        val segments: Segments =
            listOf(
                Segment.Stationary(
                    1L,
                    LocationModel(Latitude(40.7128), Longitude(-74.0060)),
                    0L,
                    3600000L,
                    3600000L,
                ),
                Segment.Stationary(
                    2L,
                    LocationModel(Latitude(40.7138), Longitude(-74.0070)),
                    3600000L,
                    7200000L,
                    3600000L,
                ),
            )
        val result = formatter.format(segments)
        assertEquals(0, result.size)
    }
}
