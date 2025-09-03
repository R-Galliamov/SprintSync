package com.developers.sprintsync.di

import com.developers.sprintsync.data.database.AppDb
import com.developers.sprintsync.data.components.IdGenerator
import com.developers.sprintsync.data.workout_plan.dao.WorkoutPlanDao
import com.developers.sprintsync.data.workout_plan.data_source.local.LocalWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.data_source.local.RoomWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.data_source.remote.FBWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.data_source.remote.RemoteWorkoutPlanDataSource
import com.developers.sprintsync.data.workout_plan.repository.WorkoutPlanRepositoryImpl
import com.developers.sprintsync.domain.workouts_plan.repository.WorkoutPlanRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WorkoutPlanModule {

    @Provides
    fun provideFirebase(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideDao(db: AppDb): WorkoutPlanDao = db.workoutPlanDao()

    @Provides
    fun provideRemoteDataSource(db: FirebaseFirestore): RemoteWorkoutPlanDataSource = FBWorkoutPlanDataSource(db)

    @Provides
    fun provideLocalDataSource(dao: WorkoutPlanDao): LocalWorkoutPlanDataSource = RoomWorkoutPlanDataSource(dao)

    @Provides
    fun provideRepository(
        localDataSource: LocalWorkoutPlanDataSource,
        remoteDataSource: RemoteWorkoutPlanDataSource,
        idGenerator: IdGenerator,
    ): WorkoutPlanRepository = WorkoutPlanRepositoryImpl(localDataSource, remoteDataSource, idGenerator)
}