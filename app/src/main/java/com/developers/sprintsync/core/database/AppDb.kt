package com.developers.sprintsync.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developers.sprintsync.data.track.database.dao.TrackDao
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.data.track.database.util.converter.SegmentsTypeConverter
import com.developers.sprintsync.data.track_preview.source.database.dao.TrackPreviewDao
import com.developers.sprintsync.data.track_preview.source.database.dto.TrackPreviewEntity
import com.developers.sprintsync.data.workout_plan.converter.PlanDaysTypeConverter
import com.developers.sprintsync.data.workout_plan.dao.WorkoutPlanDao
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto

// TODO move folder to data
@Database(
    entities = [TrackEntity::class, TrackPreviewEntity::class, WorkoutPlanDto::class],
    version = 1
)
@TypeConverters(SegmentsTypeConverter::class, PlanDaysTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun trackPreviewDao(): TrackPreviewDao

    abstract fun workoutPlanDao(): WorkoutPlanDao
}
