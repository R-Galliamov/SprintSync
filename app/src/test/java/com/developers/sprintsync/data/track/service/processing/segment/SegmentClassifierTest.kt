package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.domain.track.model.Latitude
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.domain.track.model.Longitude
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SegmentClassifierTest {

    private lateinit var classifier: SegmentClassifier

    // --- Fake DistanceCalculator ---
    class FakeDistanceCalculator(var distance: Float) : DistanceCalculator {
        override fun distanceBetweenInMeters(start: LocationModel, end: LocationModel): Float {
            return distance
        }
    }

    // Helper to create dummy LocationModel (coordinates irrelevant with fake calculator)
    private fun createLocation() = LocationModel(Latitude(0.0), Longitude(0.0))

    @BeforeEach
    fun setUp() {
        // default distance > threshold to start as Active
        classifier = SegmentClassifier(FakeDistanceCalculator(distance = 10f))
    }

    @Test
    fun `Movement above threshold classifies as Active`() {
        val fakeCalculator = FakeDistanceCalculator(5f) // > 2m
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `Movement below threshold  first instance  lastType was Active`() {
        val fakeCalculator = FakeDistanceCalculator(1f) // < 2m
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result) // noise, keep Active
    }

    @Test
    fun `Movement below threshold  first instance  lastType was Stationary`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        // simulate previous stationary confirmed
        repeat(3) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `Movement below threshold  confirms stationary after threshold count  lastType was Active`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(2) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `Movement below threshold  confirms stationary after threshold count  lastType was Stationary`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(2) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `Movement below threshold  below confirmation count  maintains last Active state`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `Movement below threshold  below confirmation count  maintains last Stationary state`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(3) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `Movement at exactly DISTANCE THRESHOLD METERS`() {
        val fakeCalculator = FakeDistanceCalculator(2f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `Movement just below DISTANCE THRESHOLD METERS`() {
        val fakeCalculator = FakeDistanceCalculator(1.9f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `Sequence  Active    Stationary  noise     Stationary  confirmed `() {
        val fakeCalculator = FakeDistanceCalculator(5f) // first movement
        classifier = SegmentClassifier(fakeCalculator)
        assertEquals(SegmentType.Active, classifier.classifySegment(createLocation(), createLocation()))

        fakeCalculator.distance = 1f // below threshold
        assertEquals(SegmentType.Active, classifier.classifySegment(createLocation(), createLocation()))
        assertEquals(SegmentType.Active, classifier.classifySegment(createLocation(), createLocation()))
        assertEquals(SegmentType.Stationary, classifier.classifySegment(createLocation(), createLocation()))
    }

    @Test
    fun `Sequence  Stationary    Active    Stationary  noise `() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(3) { classifier.classifySegment(createLocation(), createLocation()) }
        assertEquals(SegmentType.Stationary, classifier.classifySegment(createLocation(), createLocation()))

        fakeCalculator.distance = 5f
        assertEquals(SegmentType.Active, classifier.classifySegment(createLocation(), createLocation()))

        fakeCalculator.distance = 1f
        assertEquals(SegmentType.Active, classifier.classifySegment(createLocation(), createLocation()))
    }

    @Test
    fun `Zero distance between locations`() {
        val fakeCalculator = FakeDistanceCalculator(0f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `stationaryCounter reaches STATIONARY CONFIRMATION COUNT exactly`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(2) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `stationaryCounter exceeds STATIONARY CONFIRMATION COUNT`() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        repeat(4) { classifier.classifySegment(createLocation(), createLocation()) }
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Stationary, result)
    }

    @Test
    fun `Initial state  lastType is Active  default `() {
        val fakeCalculator = FakeDistanceCalculator(1f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }

    @Test
    fun `Test with large distance values`() {
        val fakeCalculator = FakeDistanceCalculator(1000f)
        classifier = SegmentClassifier(fakeCalculator)
        val result = classifier.classifySegment(createLocation(), createLocation())
        assertEquals(SegmentType.Active, result)
    }
}
