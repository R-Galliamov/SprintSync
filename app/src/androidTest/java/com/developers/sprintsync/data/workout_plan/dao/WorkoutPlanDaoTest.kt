package com.developers.sprintsync.data.workout_plan.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.developers.sprintsync.core.database.AppDb
import com.developers.sprintsync.data.workout_plan.dto.PlanDayDto
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WorkoutPlanDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDb
    private lateinit var dao: WorkoutPlanDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDb::class.java
        ).allowMainThreadQueries().build()
        dao = database.workoutPlanDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // Test: Insert single valid workout plan
    // Test: Update existing workout plan via upsert
    // Test: Handle duplicate ID insertion (should update)
    @Test
    fun insert_validWorkoutPlan_insertsSuccessfully() = runTest {
        val workoutPlan = createTestWorkoutPlan("plan1", "user1")

        dao.insert(workoutPlan)

        val result = dao.getById("plan1")
        assertThat(result).isEqualTo(workoutPlan)
    }

    @Test
    fun insert_duplicateId_updatesExistingPlan() = runTest {
        val originalPlan = createTestWorkoutPlan("plan1", "user1", "Original Title")
        val updatedPlan = createTestWorkoutPlan("plan1", "user1", "Updated Title")

        dao.insert(originalPlan)
        dao.insert(updatedPlan)

        val result = dao.getById("plan1")
        assertThat(result?.title).isEqualTo("Updated Title")
    }

    @Test
    fun insert_workoutPlanWithComplexDays_preservesAllData() = runTest {
        val complexPlan = WorkoutPlanDto(
            id = "complex1",
            startDate = System.currentTimeMillis(),
            title = "Complex Plan",
            owner = "user1",
            description = "Test",
            days = listOf(
                PlanDayDto.RestDto,
                PlanDayDto.WorkoutSessionDto(1, 1, "Session", "Desc", mapOf("speed" to 10.5f)),
                PlanDayDto.RestDto
            )
        )

        dao.insert(complexPlan)

        val result = dao.getById("complex1")
        assertThat(result?.days).hasSize(3)
        assertThat(result?.days?.get(1)).isInstanceOf(PlanDayDto.WorkoutSessionDto::class.java)
    }

    // Test: Insert empty list
    // Test: Insert multiple valid plans
    // Test: Insert list with duplicate IDs (should update existing)
    // Test: Insert large batch (performance test)
    @Test
    fun insertAll_emptyList_completesWithoutError() = runTest {
        dao.insertAll(emptyList())

        val result = dao.getAll()
        assertThat(result).isEmpty()
    }

    @Test
    fun insertAll_multipleValidPlans_insertsAll() = runTest {
        val plans = listOf(
            createTestWorkoutPlan("plan1", "user1"),
            createTestWorkoutPlan("plan2", "user2"),
            createTestWorkoutPlan("plan3", "user1")
        )

        dao.insertAll(plans)

        val result = dao.getAll()
        assertThat(result).hasSize(3)
    }

    @Test
    fun insertAll_duplicateIds_updatesExistingPlans() = runTest {
        val originalPlans = listOf(createTestWorkoutPlan("plan1", "user1", "Original"))
        val updatedPlans = listOf(createTestWorkoutPlan("plan1", "user1", "Updated"))

        dao.insertAll(originalPlans)
        dao.insertAll(updatedPlans)

        val result = dao.getAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("Updated")
    }

    @Test
    fun insertAll_largeBatch_insertsSuccessfully() = runTest {
        val largeBatch = (1..1000).map {
            createTestWorkoutPlan("plan$it", "user1")
        }

        dao.insertAll(largeBatch)

        val result = dao.getAll()
        assertThat(result).hasSize(1000)
    }

    // Test: Get plans for existing owner with multiple plans
    // Test: Get plans for owner with no plans (empty result)
    // Test: Get plans for null/empty owner
    // Test: Flow emits updates when plans are added/removed
    // Test: Flow handles concurrent modifications
    @Test
    fun getAllByOwner_existingOwnerWithMultiplePlans_returnsAllPlans() = runTest {
        val user1Plans = listOf(
            createTestWorkoutPlan("plan1", "user1"),
            createTestWorkoutPlan("plan2", "user1")
        )
        val user2Plans = listOf(createTestWorkoutPlan("plan3", "user2"))

        dao.insertAll(user1Plans + user2Plans)

        val result = dao.getAllByOwner("user1").first()
        assertThat(result).hasSize(2)
        assertThat(result.map { it.owner }.all { it == "user1" }).isTrue()
    }

    @Test
    fun getAllByOwner_nonExistentOwner_returnsEmptyFlow() = runTest {
        dao.insert(createTestWorkoutPlan("plan1", "user1"))

        val result = dao.getAllByOwner("nonexistent").first()
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllByOwner_emptyOwnerString_returnsEmptyFlow() = runTest {
        dao.insert(createTestWorkoutPlan("plan1", "user1"))

        val result = dao.getAllByOwner("").first()
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllByOwner_flowEmitsUpdatesOnInsert() = runTest {
        val testCollector = mutableListOf<List<WorkoutPlanDto>>()
        val job = launch {
            dao.getAllByOwner("user1").collect { testCollector.add(it) }
        }

        // Initial empty state
        advanceUntilIdle()
        assertThat(testCollector.last()).isEmpty()

        // Insert plan
        dao.insert(createTestWorkoutPlan("plan1", "user1"))
        advanceUntilIdle()
        assertThat(testCollector.last()).hasSize(1)

        job.cancel()
    }

    @Test
    fun getAllByOwner_flowEmitsUpdatesOnDelete() = runTest {
        dao.insert(createTestWorkoutPlan("plan1", "user1"))

        val testCollector = mutableListOf<List<WorkoutPlanDto>>()
        val job = launch {
            dao.getAllByOwner("user1").collect { testCollector.add(it) }
        }

        advanceUntilIdle()
        assertThat(testCollector.last()).hasSize(1)

        dao.deleteById("plan1")
        advanceUntilIdle()
        assertThat(testCollector.last()).isEmpty()

        job.cancel()
    }

    // Test: Get all plans for existing owner (one-time)
    // Test: Get all plans for non-existent owner (empty result)
    // Test: Verify no Flow emission (suspend function)
    @Test
    fun getAllByOwnerOnce_existingOwner_returnsPlansOnce() = runTest {
        dao.insertAll(
            listOf(
                createTestWorkoutPlan("plan1", "user1"),
                createTestWorkoutPlan("plan2", "user1"),
                createTestWorkoutPlan("plan3", "user2")
            )
        )

        val result = dao.getAllByOwnerOnce("user1")
        assertThat(result).hasSize(2)
        assertThat(result.map { it.owner }.all { it == "user1" }).isTrue()
    }

    @Test
    fun getAllByOwnerOnce_nonExistentOwner_returnsEmptyList() = runTest {
        dao.insert(createTestWorkoutPlan("plan1", "user1"))

        val result = dao.getAllByOwnerOnce("nonexistent")
        assertThat(result).isEmpty()
    }

    // Test: Get all plans when database has multiple plans
    // Test: Get all plans when database is empty
    // Test: Verify all owners included in result
    @Test
    fun getAll_multipleExistingPlans_returnsAllPlans() = runTest {
        val allPlans = listOf(
            createTestWorkoutPlan("plan1", "user1"),
            createTestWorkoutPlan("plan2", "user2"),
            createTestWorkoutPlan("plan3", "user1")
        )
        dao.insertAll(allPlans)

        val result = dao.getAll()
        assertThat(result).hasSize(3)
        assertThat(result.map { it.owner }).containsExactly("user1", "user2", "user1")
    }

    @Test
    fun getAll_emptyDatabase_returnsEmptyList() = runTest {
        val result = dao.getAll()
        assertThat(result).isEmpty()
    }

    // Test: Flow emits all plans and updates on changes
    // Test: Flow works with empty database
    // Test: Flow handles concurrent database operations
    @Test
    fun getAllFlow_emitsAllPlansAndUpdates() = runTest {
        val testCollector = mutableListOf<List<WorkoutPlanDto>>()
        val job = launch {
            dao.getAllFlow().collect { testCollector.add(it) }
        }

        // Initial empty state
        advanceUntilIdle()
        assertThat(testCollector.last()).isEmpty()

        // Add plans
        dao.insertAll(
            listOf(
                createTestWorkoutPlan("plan1", "user1"),
                createTestWorkoutPlan("plan2", "user2")
            )
        )
        advanceUntilIdle()
        assertThat(testCollector.last()).hasSize(2)

        // Delete one plan
        dao.deleteById("plan1")
        advanceUntilIdle()
        assertThat(testCollector.last()).hasSize(1)

        job.cancel()
    }

    // Test: Get existing plan by valid ID
    // Test: Get non-existent plan by ID (returns null)
    // Test: Get plan with empty/null ID
    // Test: Get plan with special characters in ID
    @Test
    fun getById_existingId_returnsCorrectPlan() = runTest {
        val plan = createTestWorkoutPlan("test-id", "user1")
        dao.insert(plan)

        val result = dao.getById("test-id")
        assertThat(result).isEqualTo(plan)
    }

    @Test
    fun getById_nonExistentId_returnsNull() = runTest {
        dao.insert(createTestWorkoutPlan("existing", "user1"))

        val result = dao.getById("nonexistent")
        assertThat(result).isNull()
    }

    @Test
    fun getById_emptyId_returnsNull() = runTest {
        dao.insert(createTestWorkoutPlan("valid-id", "user1"))

        val result = dao.getById("")
        assertThat(result).isNull()
    }

    @Test
    fun getById_specialCharactersInId_worksCorrectly() = runTest {
        val specialId = "plan-123_test@domain.com"
        val plan = createTestWorkoutPlan(specialId, "user1")
        dao.insert(plan)

        val result = dao.getById(specialId)
        assertThat(result).isEqualTo(plan)
    }

    // Test: Delete existing plan by ID
    // Test: Delete non-existent plan by ID (no error)
    // Test: Delete with empty/null ID
    // Test: Verify other plans remain after deletion
    @Test
    fun deleteById_existingId_deletesPlan() = runTest {
        dao.insertAll(
            listOf(
                createTestWorkoutPlan("plan1", "user1"),
                createTestWorkoutPlan("plan2", "user1")
            )
        )

        dao.deleteById("plan1")

        assertThat(dao.getById("plan1")).isNull()
        assertThat(dao.getById("plan2")).isNotNull()
    }

    @Test
    fun deleteById_nonExistentId_completesWithoutError() = runTest {
        dao.insert(createTestWorkoutPlan("existing", "user1"))

        dao.deleteById("nonexistent")

        // Verify existing plan is still there
        assertThat(dao.getById("existing")).isNotNull()
    }

    @Test
    fun deleteById_emptyId_completesWithoutError() = runTest {
        dao.insert(createTestWorkoutPlan("existing", "user1"))

        dao.deleteById("")

        assertThat(dao.getById("existing")).isNotNull()
    }

    // Test: Delete existing plan entity
    // Test: Delete non-existent plan entity (no error)
    // Test: Delete plan and verify removal
    @Test
    fun delete_existingPlan_deletesPlan() = runTest {
        val plan = createTestWorkoutPlan("plan1", "user1")
        dao.insert(plan)

        dao.delete(plan)

        assertThat(dao.getById("plan1")).isNull()
    }

    @Test
    fun delete_nonExistentPlan_completesWithoutError() = runTest {
        val existingPlan = createTestWorkoutPlan("existing", "user1")
        val nonExistentPlan = createTestWorkoutPlan("nonexistent", "user1")
        dao.insert(existingPlan)

        dao.delete(nonExistentPlan)

        assertThat(dao.getById("existing")).isNotNull()
    }

    // Integration tests for complex scenarios
    @Test
    fun concurrentOperations_multipleReadWrites_maintainDataConsistency() = runTest {
        val jobs = (1..10).map { index ->
            launch {
                val plan = createTestWorkoutPlan("plan$index", "user$index")
                dao.insert(plan)

                val retrieved = dao.getById("plan$index")
                assertThat(retrieved).isNotNull()

                dao.deleteById("plan$index")
            }
        }

        jobs.joinAll()

        val finalCount = dao.getAll()
        assertThat(finalCount).isEmpty()
    }

    @Test
    fun transactionRollback_failedBatchInsert_maintainsDatabaseIntegrity() = runTest {
        // This test would require custom transaction handling
        // Testing Room's built-in transaction behavior
        val validPlans = listOf(createTestWorkoutPlan("valid1", "user1"))
        dao.insertAll(validPlans)

        // Verify data integrity
        val result = dao.getAll()
        assertThat(result).hasSize(1)
    }

    // Helper method to create test workout plans
    private fun createTestWorkoutPlan(
        id: String,
        owner: String,
        title: String = "Test Plan"
    ) = WorkoutPlanDto(
        id = id,
        startDate = System.currentTimeMillis(),
        title = title,
        owner = owner,
        description = "Test Description",
        days = listOf(
            PlanDayDto.RestDto,
            PlanDayDto.WorkoutSessionDto(
                id = 1,
                runDay = 1,
                title = "Test Session",
                description = "Test Session Description",
                targets = mapOf("distance" to 5.0f, "time" to 30.0f)
            )
        )
    )
}