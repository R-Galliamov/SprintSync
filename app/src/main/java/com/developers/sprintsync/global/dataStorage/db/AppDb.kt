package com.developers.sprintsync.global.dataStorage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developers.sprintsync.tracking.dataStorage.db.converter.BitmapConverter
import com.developers.sprintsync.tracking.dataStorage.db.converter.SegmentsTypeConverter
import com.developers.sprintsync.tracking.dataStorage.db.dao.TrackDao
import com.developers.sprintsync.tracking.dataStorage.db.dto.TrackEntity
import com.developers.sprintsync.statistics.data.db.dao.DailyGoalDao
import com.developers.sprintsync.statistics.data.db.dto.DailyGoalEntity

@Database(entities = [TrackEntity::class, DailyGoalEntity::class], version = 1)
@TypeConverters(SegmentsTypeConverter::class, BitmapConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun dailyGoalDao(): DailyGoalDao
}
