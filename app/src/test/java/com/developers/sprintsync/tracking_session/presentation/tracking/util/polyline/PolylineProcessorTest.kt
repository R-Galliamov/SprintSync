package com.developers.sprintsync.tracking_session.presentation.tracking.util.polyline

import android.graphics.Color
import com.developers.sprintsync.core.util.log.TimberAppLogger
import com.developers.sprintsync.domain.track.model.Latitude
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.Longitude
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineFormatter
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineOptionsCreator
import com.developers.sprintsync.presentation.workout_session.active.util.polyline.PolylineProcessor
import com.google.android.gms.maps.model.LatLng
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for the PolylineProcessor class, ensuring it correctly generates PolylineOptions
 * from segments using real instances of PolylineFormatter and PolylineOptionsCreator.
 */
class PolylineProcessorTest {
    private lateinit var processor: PolylineProcessor
    private val polylineFormatter = PolylineFormatter(TimberAppLogger())
    private val polylineOptionsCreator = PolylineOptionsCreator()

    @BeforeEach
    fun setUp() {
        processor = PolylineProcessor(polylineOptionsCreator, polylineFormatter)
    }

    /**
     * Tests that an empty list of segments results in an empty list of PolylineOptions.
     */
    @Test
    fun generatePolylinesWithEmptySegmentsReturnsEmptyList() {
        val segments: List<Segment> = emptyList()
        val result = processor.generatePolylines(segments)
        assertEquals(0, result.size)
    }

    /**
     * Tests that a single active segment is correctly processed into one PolylineOptions object.
     */
    @Test
    fun generatePolylinesWithSingleActiveSegmentCreatesOnePolylineOption() {
        val segments: List<Segment> =
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
        val result = processor.generatePolylines(segments)
        assertEquals(1, result.size)
        assertEquals(listOf(LatLng(40.7128, -74.0060), LatLng(40.7138, -74.0070)), result[0].points)
        assertEquals(Color.RED, result[0].color)
        assertEquals(7.5f, result[0].width)
    }

    /**
     * Tests that multiple continuous active segments are processed into one PolylineOptions object.
     */
    @Test
    fun generatePolylinesWithContinuousActiveSegmentsCreatesOnePolylineOption() {
        val segments: List<Segment> =
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
        val result = processor.generatePolylines(segments)
        assertEquals(1, result.size)
        assertEquals(
            listOf(LatLng(40.7128, -74.0060), LatLng(40.7138, -74.0070), LatLng(40.7148, -74.0080)),
            result[0].points,
        )
        assertEquals(Color.RED, result[0].color)
        assertEquals(7.5f, result[0].width)
    }

    /**
     * Tests that active segments with a break are processed into multiple PolylineOptions objects.
     */
    @Test
    fun generatePolylinesWithBreakCreatesMultiplePolylineOptions() {
        val segments: List<Segment> =
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
                    LocationModel(Latitude(40.7140), Longitude(-74.0080)),
                    7200000L,
                    LocationModel(Latitude(40.7150), Longitude(-74.0090)),
                    10800000L,
                    3600000L,
                    1000f,
                    300f,
                    100f,
                ),
            )
        val result = processor.generatePolylines(segments)
        assertEquals(2, result.size)
        assertEquals(listOf(LatLng(40.7128, -74.0060), LatLng(40.7138, -74.0070)), result[0].points)
        assertEquals(listOf(LatLng(40.7140, -74.0080), LatLng(40.7150, -74.0090)), result[1].points)
        assertEquals(Color.RED, result[0].color)
        assertEquals(7.5f, result[0].width)
        assertEquals(Color.RED, result[1].color)
        assertEquals(7.5f, result[1].width)
    }

    /**
     * Tests that a stationary segment between active segments results in multiple PolylineOptions objects.
     */
    @Test
    fun generatePolylinesWithStationarySegmentCreatesMultiplePolylineOptions() {
        val segments: List<Segment> =
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
        val result = processor.generatePolylines(segments)
        assertEquals(2, result.size)
        assertEquals(listOf(LatLng(40.7128, -74.0060), LatLng(40.7138, -74.0070)), result[0].points)
        assertEquals(listOf(LatLng(40.7138, -74.0070), LatLng(40.7148, -74.0080)), result[1].points)
        assertEquals(Color.RED, result[0].color)
        assertEquals(7.5f, result[0].width)
        assertEquals(Color.RED, result[1].color)
        assertEquals(7.5f, result[1].width)
    }
}
