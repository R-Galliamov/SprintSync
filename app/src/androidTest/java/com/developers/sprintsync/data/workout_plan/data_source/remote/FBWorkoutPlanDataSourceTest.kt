package com.developers.sprintsync.data.workout_plan.data_source.remote

import com.developers.sprintsync.data.workout_plan.data_source.constants.FirebaseCollections
import com.developers.sprintsync.data.workout_plan.data_source.constants.OwnerConstants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.common.truth.Truth.assertThat
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class FBWorkoutPlanDataSourceTest {

    companion object {
        lateinit var db: FirebaseFirestore

        @JvmStatic
        @BeforeClass
        fun globalSetup() {
            db = FirebaseFirestore.getInstance()
            db.useEmulator("10.0.2.2", 8080)
            db.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
        }
    }

    private lateinit var dataSource: FBWorkoutPlanDataSource

    @Before
    fun setup() {
        dataSource = FBWorkoutPlanDataSource(db)
    }

    @After
    fun tearDown() = runBlocking {
        val snap = db.collection(FirebaseCollections.WORKOUT_PLANS).get().await()
        for (doc in snap.documents) {
            doc.reference.delete().await()
        }
    }

    @Test
    fun save_and_getById_returnsInsertedPlan() = runBlocking {
        val plan = testPlan("p1", "user1")
        dataSource.save(plan)

        val loaded = dataSource.getById("p1")
        assertThat(loaded).isEqualTo(plan)
    }

    @Test
    fun saveAll_and_getAllOwnerPlans_returnsAllPlans(): Unit = runBlocking {
        val plans = listOf(
            testPlan("p1", "user1"),
            testPlan("p2", "user1"),
            testPlan("p3", "user2")
        )
        dataSource.saveAll(plans)

        val user1Plans = dataSource.getAllOwnerPlans("user1")
        assertThat(user1Plans.map { it.id }).containsExactly("p1", "p2")
        assertThat(user1Plans.map { it.owner }.distinct()).containsExactly("user1")

        val user2Plans = dataSource.getAllOwnerPlans("user2")
        assertThat(user2Plans.map { it.id }).containsExactly("p3")
    }

    @Test
    fun getAllSystemPlans_onlySystemOwner(): Unit = runBlocking {
        val systemPlan = testPlan("sys1", OwnerConstants.SYSTEM)
        val userPlan = testPlan("user1plan", "user1")
        dataSource.saveAll(listOf(systemPlan, userPlan))

        val result = dataSource.getAllSystemPlans()
        assertThat(result.map { it.id }).containsExactly("sys1")
        assertThat(result.map { it.owner }).containsExactly(OwnerConstants.SYSTEM)
    }

    @Test
    fun deleteById_removesPlan() = runBlocking {
        val plan = testPlan("del1", "user1")
        dataSource.save(plan)
        dataSource.deleteById("del1")

        val loaded = dataSource.getById("del1")
        assertThat(loaded).isNull()
    }

    @Test
    fun getById_nonexistent_returnsNull() = runBlocking {
        val result = dataSource.getById("not_exists")
        assertThat(result).isNull()
    }

    @Test
    fun saveAll_emptyList_noCrash() = runBlocking {
        dataSource.saveAll(emptyList())
        val allPlans = dataSource.getAllOwnerPlans("any")
        assertThat(allPlans).isEmpty()
    }

    // Helper
    private fun testPlan(id: String, owner: String) = WorkoutPlanDto(
        id = id,
        startDate = System.currentTimeMillis(),
        title = "Test Plan $id",
        owner = owner,
        description = "desc",
        days = emptyList()
    )
}
