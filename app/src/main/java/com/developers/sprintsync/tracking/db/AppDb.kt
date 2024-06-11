package com.developers.sprintsync.tracking.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developers.sprintsync.tracking.db.converter.SegmentsTypeConverter
import com.developers.sprintsync.tracking.db.dao.TrackDao
import com.developers.sprintsync.tracking.db.dto.TrackEntity

@Database(entities = [TrackEntity::class], version = 1)
@TypeConverters(SegmentsTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}
