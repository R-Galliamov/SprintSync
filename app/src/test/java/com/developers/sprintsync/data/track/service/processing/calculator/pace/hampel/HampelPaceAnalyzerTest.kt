package com.developers.sprintsync.data.track.service.processing.calculator.pace.hampel

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
import com.developers.sprintsync.domain.track.model.LocationModel
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class HampelPaceAnalyzerTest {

    private val eps = 1e-5f

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @After
    fun tearDown() = unmockkAll()

    // Helpers
    private fun loc(): LocationModel = mockk()
    private fun tp(timeMs: Long, loc: LocationModel): TrackPoint =
        mockk<TrackPoint>().apply {
            every { this@apply.timeMs } returns timeMs
            every { this@apply.location } returns loc
        }

    private fun analyzer(
        dc: DistanceCalculator = mockk(),
        hf: HampelFilter = mockk(),
        ema: EmaSmoother = mockk(),
        max: Float = 7.5f,
        logger: AppLogger = mockk(relaxed = true)
    ) = HampelPaceAnalyzer(dc, hf, ema, max, logger)

    // 1) reset() state verification
    @Test
    fun `reset clears state and dependencies`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>(relaxUnitFun = true)
        val ema = mockk<EmaSmoother>(relaxUnitFun = true)

        val a = analyzer(dc, hf, ema)

        val l1 = loc()
        val l2 = loc()
        every { dc.distM(any(), any()) } returns 10f
        every { hf.addAndClamp(any()) } answers { firstArg() }
        every { ema.update(any(), any()) } answers { secondArg() }
        every { ema.value } returnsMany listOf(0f, 1f)

        a.add(tp(1_000L, l1))
        val before = a.add(tp(3_000L, l2))
        assertTrue(before.speedMps > 0f)
        assertTrue(before.totalDistanceM > 0f)
        assertTrue(before.totalTimeSec > 0f)

        a.reset()
        every { ema.value } returns 0f
        val after = a.add(tp(9_000L, l1))
        assertEquals(0f, after.speedMps, eps)
        assertEquals(0f, after.totalDistanceM, eps)
        assertEquals(0f, after.totalTimeSec, eps)

        verify { hf.clear() }
        verify { ema.reset() }
    }


    // 2) add() first TrackPoint
    @Test
    fun `first add returns initial snapshot`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        every { ema.value } returns 0f

        val a = analyzer(dc, hf, ema)
        val s = a.add(tp(1_000L, loc()))
        assertEquals(0f, s.speedMps, eps)
        assertNull(s.paceMinPerKm)
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(0f, s.totalTimeSec, eps)
        verify(exactly = 0) { dc.distM(any(), any()) }
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 3) add() subsequent TrackPoint with valid data
    @Test
    fun `second add valid updates totals and calls deps`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()

        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 10f
        every { hf.addAndClamp(5f) } returns 4.5f
        every { ema.update(2f, 4.5f) } returns 4.2f
        every { ema.value } returnsMany listOf(0f, 4.2f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(3_000L, l2))

        assertEquals(4.2f, s.speedMps, eps)
        assertEquals(10f, s.totalDistanceM, eps)
        assertEquals(2f, s.totalTimeSec, eps)
        verify { dc.distM(l1, l2) }
        verify { hf.addAndClamp(5f) }
        verify { ema.update(2f, 4.5f) }
    }

    // 4) add() with zero time difference
    @Test
    fun `dtSec zero returns current snapshot no processing`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        val l = loc()
        val s1 = a.add(tp(1_000L, l))
        val s2 = a.add(tp(1_000L, l))  // same time

        assertEquals(s1.speedMps, s2.speedMps, eps)
        assertEquals(s1.totalDistanceM, s2.totalDistanceM, eps)
        assertEquals(s1.totalTimeSec, s2.totalTimeSec, eps)
        verify(exactly = 0) { dc.distM(any(), any()) }
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 5) add() with negative time difference (coerced to 0)
    @Test
    fun `dtSec negative coerced to zero behaves like zero`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        val l = loc()
        val s1 = a.add(tp(3_000L, l))
        val s2 = a.add(tp(1_000L, l))  // earlier time → coerced to 0

        assertEquals(s1.totalTimeSec, s2.totalTimeSec, eps)
        verify(exactly = 0) { dc.distM(any(), any()) }
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 6) add() with non-finite dtSec is unreachable; verify snapshot unchanged via zero-time path
    @Test
    fun `dtSec non-finite not possible here treated as no-op`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        val l = loc()
        val s1 = a.add(tp(1_000L, l))
        val s2 = a.add(tp(1_000L, l))
        assertEquals(s1.totalTimeSec, s2.totalTimeSec, eps)
    }

    // 7) add() with zero distance
    @Test
    fun `dist zero segSpeed zero increments only time`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 0f
        every { hf.addAndClamp(0f) } returns 0f
        every { ema.update(2f, 0f) } returns 0f
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(3_000L, l2))
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(2f, s.totalTimeSec, eps)
        verify { hf.addAndClamp(0f) }
        verify { ema.update(2f, 0f) }
    }

    // 8) add() with negative distance
    @Test
    fun `dist negative ignored snapshot unchanged`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns -5f
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(2_000L, l2))
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(0f, s.totalTimeSec, eps)
        verify { dc.distM(l1, l2) }
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 9) add() with non-finite distance
    @Test
    fun `dist non-finite ignored`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns Float.NaN
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(2_000L, l2))
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(0f, s.totalTimeSec, eps)
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 10) add() with non-finite segment speed
    @Test
    fun `segSpeed non-finite ignored`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns Float.POSITIVE_INFINITY
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(2_000L, l2))
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(0f, s.totalTimeSec, eps)
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 11) segSpeed exceeding maxSpeedMps is capped before Hampel
    @Test
    fun `segSpeed capped before hampel`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 100f              // dt = 1s → raw 100 mps
        every { hf.addAndClamp(7.5f) } returns 7.5f
        every { ema.update(1f, 7.5f) } returns 7.5f
        every { ema.value } returnsMany listOf(0f, 7.5f)

        val a = analyzer(dc, hf, ema)                        // default max=7.5
        a.add(tp(1_000L, l1))
        val s = a.add(tp(2_000L, l2))
        assertEquals(7.5f, s.speedMps, eps)
        verify { hf.addAndClamp(7.5f) }
    }

    // 12) negative segSpeed path unreachable; ensure negative distance short-circuits earlier
    @Test
    fun `negative segSpeed unreachable due to early guard`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns -1f
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        verify(exactly = 0) { hf.addAndClamp(any()) }
        verify(exactly = 0) { ema.update(any(), any()) }
    }

    // 13) hampelFilter returns NaN → filtered becomes 0f
    @Test
    fun `hampel NaN coerced to zero before ema`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 10f
        every { hf.addAndClamp(5f) } returns Float.NaN
        every { ema.update(2f, 0f) } returns 0f
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        a.add(tp(3_000L, l2))
        verify { ema.update(2f, 0f) }
    }

    // 14) segSpeed zero case
    @Test
    fun `segSpeed zero handled and totals updated correctly`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 0f
        every { hf.addAndClamp(0f) } returns 0f
        every { ema.update(1f, 0f) } returns 0f
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        val s = a.add(tp(2_000L, l2))
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(1f, s.totalTimeSec, eps)
    }

    // 15) multiple sequential points accumulate correctly
    @Test
    fun `multiple adds accumulate`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val l3 = loc()
        every { dc.distM(l1, l2) } returns 10f
        every { dc.distM(l2, l3) } returns 0f
        every { hf.addAndClamp(any()) } answers { firstArg() }
        var last = 0f
        every { ema.update(any(), any()) } answers { last = secondArg<Float>(); last }
        every { ema.value } returns last

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        val s = a.add(tp(4_000L, l3))
        assertEquals(10f, s.totalDistanceM, eps) // second segment distance not added
        assertEquals(3f, s.totalTimeSec, eps)    // time always accumulates
    }

    // 16) add() after reset() behaves like first add
    @Test
    fun `add after reset behaves like first`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>(relaxUnitFun = true)
        val ema = mockk<EmaSmoother>(relaxUnitFun = true)
        every { ema.value } returnsMany listOf(0f, 0f)

        val a = analyzer(dc, hf, ema)
        val l = loc()
        a.add(tp(1_000L, l))
        a.reset()
        val s = a.add(tp(2_000L, l))
        assertEquals(0f, s.speedMps, eps)
        assertEquals(0f, s.totalDistanceM, eps)
        assertEquals(0f, s.totalTimeSec, eps)
    }

    // 17) snapshot() when EMA speed is zero
    @Test
    fun `snapshot ema zero gives pace null`() {
        val a = analyzer(mockk(), mockk(), mockk<EmaSmoother>().apply { every { value } returns 0f })
        val s = a.add(tp(1_000L, loc()))
        assertEquals(0f, s.speedMps, eps)
        assertNull(s.paceMinPerKm)
    }

    // 18) snapshot() when EMA speed is positive
    @Test
    fun `snapshot ema positive gives correct pace`() {
        val ema = mockk<EmaSmoother>()
        every { ema.value } returns 5f
        val a = analyzer(mockk(), mockk(), ema)
        val s = a.add(tp(1_000L, loc()))
        assertEquals(5f, s.speedMps, eps)
        assertEquals((1000f / 5f) / 60f, s.paceMinPerKm!!, eps)
    }

    // 19) maxSpeedMps default used
    @Test
    fun `default maxSpeed caps to 7_5`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 1000f
        every { hf.addAndClamp(7.5f) } returns 7.5f
        every { ema.update(1f, 7.5f) } returns 7.5f
        every { ema.value } returnsMany listOf(0f, 7.5f)

        val a = analyzer(dc, hf, ema) // default 7.5
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        verify { hf.addAndClamp(7.5f) }
    }

    // 20) maxSpeedMps custom value used
    @Test
    fun `custom maxSpeed is respected`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 100f
        every { hf.addAndClamp(3.0f) } returns 3.0f
        every { ema.update(1f, 3.0f) } returns 3.0f
        every { ema.value } returnsMany listOf(0f, 3.0f)

        val a = analyzer(dc, hf, ema, max = 3.0f)
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        verify { hf.addAndClamp(3.0f) }
    }

    // 21) Dependency interactions: log.d calls (not present in class; assert none)
    @Test
    fun `no logging occurs because analyzer does not call logger`() {
        val logger = mockk<AppLogger>(relaxUnitFun = true)
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        every { ema.value } returns 0f

        val a = analyzer(dc, hf, ema, logger = logger)
        a.add(tp(1_000L, loc()))
        verify { logger wasNot Called }
    }

    // 22) Dependency interactions: distCalculator.distM call with correct args
    @Test
    fun `distCalculator called with previous and current locations`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(any(), any()) } returns 5f
        every { hf.addAndClamp(5f) } returns 5f
        every { ema.update(1f, 5f) } returns 5f
        every { ema.value } returnsMany listOf(0f, 5f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        verify { dc.distM(l1, l2) }
    }

    // 23) Dependency interactions: hampelFilter.addAndClamp call
    @Test
    fun `hampel receives capped segSpeed`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 20f
        every { hf.addAndClamp(any()) } returns 7.5f
        every { ema.update(1f, 7.5f) } returns 7.5f
        every { ema.value } returnsMany listOf(0f, 7.5f)

        val a = analyzer(dc, hf, ema) // cap 7.5
        a.add(tp(1_000L, l1))
        a.add(tp(2_000L, l2))
        verify { hf.addAndClamp(7.5f) }
    }

    // 24) Dependency interactions: emaSmoother.update call
    @Test
    fun `ema update called with dtSec and filtered`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        every { dc.distM(l1, l2) } returns 6f
        every { hf.addAndClamp(3f) } returns 2.5f
        every { ema.update(2f, 2.5f) } returns 2.5f
        every { ema.value } returnsMany listOf(0f, 2.5f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(1_000L, l1))
        a.add(tp(3_000L, l2))
        verify { ema.update(2f, 2.5f) }
    }

    // 25) Very large time difference
    @Test
    fun `very large dtSec remains stable`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val dt = 10_000_000L // ~115 days
        every { dc.distM(l1, l2) } returns 1000f
        every { hf.addAndClamp(any()) } answers { firstArg() }

        var last = 0f
        every { ema.update(any(), any()) } answers { last = secondArg<Float>(); last }
        every { ema.value } answers { last }


        val a = analyzer(dc, hf, ema)
        a.add(tp(0L, l1))
        val s = a.add(tp(dt, l2))
        assertEquals(1000f, s.totalDistanceM, eps) // segSpeed>0 so distance added
        assertEquals(dt / 1000f, s.totalTimeSec, eps)
    }

    // 26) Very small positive time difference
    @Test
    fun `very small dtSec caps huge speed`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val maxSpeed = 7.5f
        every { dc.distM(l1, l2) } returns 1f
        every { hf.addAndClamp(maxSpeed) } returns maxSpeed
        every { ema.update(any(), maxSpeed) } returns maxSpeed
        every { ema.value } returnsMany listOf(0f, maxSpeed)

        val a = analyzer(dc, hf, ema, maxSpeed)
        a.add(tp(1_000_000L, l1))
        a.add(tp(1_000_100L, l2)) // 0.0001 s
        verify { hf.addAndClamp(maxSpeed) }
    }

    // 27) Very large distance
    @Test
    fun `very large distance accumulates without overflow`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val dist = 10_000_000f
        every { dc.distM(l1, l2) } returns dist
        every { hf.addAndClamp(7.5f) } returns 7.5f
        every { ema.update(1_000f, 7.5f) } returns 7.5f
        every { ema.value } returnsMany listOf(0f, 7.5f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(0L, l1))
        val s = a.add(tp(1_000_000L, l2))
        assertEquals(dist, s.totalDistanceM, eps)
        assertEquals(1000f, s.totalTimeSec, eps)
    }

    // 28) Very small positive distance
    @Test
    fun `very small distance handled`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val d = 1e-4f
        every { dc.distM(l1, l2) } returns d
        every { hf.addAndClamp(d) } returns d
        every { ema.update(1f, d) } returns d
        every { ema.value } returnsMany listOf(0f, d)

        val a = analyzer(dc, hf, ema)
        a.add(tp(0L, l1))
        val s = a.add(tp(1_000L, l2))
        assertEquals(d, s.totalDistanceM, eps)
        assertEquals(1f, s.totalTimeSec, eps)
    }

    // 29) totalTime accumulation with multiple points
    @Test
    fun `totalTime accumulates across segments`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val l3 = loc()
        val l4 = loc()
        every { dc.distM(any(), any()) } returns 1f
        every { hf.addAndClamp(any()) } answers { firstArg() }
        every { ema.update(any(), any()) } returns 1f
        every { ema.value } returnsMany listOf(0f, 1f, 1f, 1f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(0L, l1))
        a.add(tp(1_000L, l2)) // +1
        a.add(tp(3_000L, l3)) // +2
        val s = a.add(tp(6_000L, l4)) // +3
        assertEquals(6f, s.totalTimeSec, eps)
    }

    // 30) totalDist accumulates only for segSpeed gt 0
    @Test
    fun `totalDist skips zero speed segments`() {
        val dc = mockk<DistanceCalculator>()
        val hf = mockk<HampelFilter>()
        val ema = mockk<EmaSmoother>()
        val l1 = loc()
        val l2 = loc()
        val l3 = loc()
        every { dc.distM(l1, l2) } returns 10f
        every { dc.distM(l2, l3) } returns 0f
        every { hf.addAndClamp(any()) } answers { firstArg() }
        every { ema.update(any(), any()) } answers { secondArg() }
        every { ema.value } returnsMany listOf(0f, 10f)

        val a = analyzer(dc, hf, ema)
        a.add(tp(0L, l1))
        a.add(tp(1_000L, l2))
        val s = a.add(tp(2_000L, l3))
        assertEquals(10f, s.totalDistanceM, eps)
        assertEquals(2f, s.totalTimeSec, eps)
    }
}
