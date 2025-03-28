package com.developers.sprintsync.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developers.sprintsync.core.components.goal.data.database.dao.DailyGoalDao
import com.developers.sprintsync.core.components.goal.data.database.dto.DailyGoalEntity
import com.developers.sprintsync.core.components.track.data.database.dao.TrackDao
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track.data.database.util.converter.SegmentsTypeConverter
import com.developers.sprintsync.core.components.track_preview.data.database.dao.TrackPreviewDao
import com.developers.sprintsync.core.components.track_preview.data.database.dto.TrackPreviewPathEntity

@Database(entities = [TrackEntity::class, DailyGoalEntity::class, TrackPreviewPathEntity::class], version = 1)
@TypeConverters(SegmentsTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun dailyGoalDao(): DailyGoalDao

    abstract fun trackPreviewDao(): TrackPreviewDao
}
