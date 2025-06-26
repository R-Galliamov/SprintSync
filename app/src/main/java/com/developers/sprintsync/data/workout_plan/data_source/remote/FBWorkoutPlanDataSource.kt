package com.developers.sprintsync.data.workout_plan.data_source.remote

import com.developers.sprintsync.data.workout_plan.data_source.constants.FirebaseCollections
import com.developers.sprintsync.data.workout_plan.data_source.constants.OwnerConstants
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDtoConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// TODO firebase security rules
/*
match /workout_plans/{planId} {
  allow read: if resource.data.isSystem == true || resource.data.createdBy == request.auth.uid;
  allow write: if resource.data.createdBy == request.auth.uid;
}
 */

class FBWorkoutPlanDataSource (
    private val db: FirebaseFirestore,
) : RemoteWorkoutPlanDataSource {
    private val collection = db.collection(FirebaseCollections.WORKOUT_PLANS)

    override suspend fun save(dto: WorkoutPlanDto) {
        collection.document(dto.id).set(dto).await()
    }

    override suspend fun saveAll(dtos: List<WorkoutPlanDto>) {
        val batch = db.batch()
        dtos.forEach { batch.set(collection.document(it.id), it) }
        batch.commit().await()
    }

    override suspend fun getById(id: String): WorkoutPlanDto? {
        val snap = collection.document(id).get().await()
        return snap.toObject(WorkoutPlanDto::class.java)
    }

    override suspend fun getAllOwnerPlans(owner: String): List<WorkoutPlanDto> {
        val snap = collection
            .whereEqualTo(WorkoutPlanDtoConstants.FIELD_OWNER, owner)
            .get()
            .await()
        return snap.toObjects(WorkoutPlanDto::class.java).filterNotNull()
    }

    override suspend fun getAllSystemPlans(): List<WorkoutPlanDto> {
        val snap = collection
            .whereEqualTo(WorkoutPlanDtoConstants.FIELD_OWNER, OwnerConstants.SYSTEM)
            .get()
            .await()
        return snap.toObjects(WorkoutPlanDto::class.java).filterNotNull()
    }

    override suspend fun deleteById(id: String) {
        collection.document(id).delete().await()
    }

}