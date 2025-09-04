package com.developers.sprintsync.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.developers.sprintsync.data.track.database.dao.TrackDao
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.data.track.database.util.converter.SegmentsTypeConverter
import com.developers.sprintsync.data.track_preview.source.database.dao.TrackPreviewDao
import com.developers.sprintsync.data.track_preview.source.database.dto.TrackPreviewEntity

@Database(
    entities = [TrackEntity::class, TrackPreviewEntity::class],
    version = 1
)
@TypeConverters(SegmentsTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun trackPreviewDao(): TrackPreviewDao
}
