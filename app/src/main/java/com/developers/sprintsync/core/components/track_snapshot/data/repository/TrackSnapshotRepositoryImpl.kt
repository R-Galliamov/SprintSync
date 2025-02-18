package com.developers.sprintsync.core.components.track_snapshot.data.repository

import android.util.Log
import com.developers.sprintsync.core.components.track_snapshot.data.data_source.TrackSnapshotDataSource
import com.developers.sprintsync.core.components.track_snapshot.data.database.dao.TrackSnapshotDao
import com.developers.sprintsync.core.components.track_snapshot.data.database.dto.TrackSnapshotEntity
import com.developers.sprintsync.core.components.track_snapshot.data.model.TrackSnapshot
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackSnapshotRepositoryImpl
    @Inject
    constructor(
        private val dao: TrackSnapshotDao,
        private val trackSnapshotDataSource: TrackSnapshotDataSource,
    ) : TrackSnapshotRepository {
        override suspend fun saveSnapshot(snapshot: TrackSnapshot) {
            val resultPath = trackSnapshotDataSource.saveBitmapToFile(snapshot)
            resultPath.fold(
                onSuccess = { path ->
                    Log.i(TAG, "saveSnapshot: $path")
                    val entity = TrackSnapshotEntity.fromDto(snapshot, path)
                    dao.insertSnapshot(entity)
                },
                onFailure = { error ->
                    Log.e(TAG, "saveSnapshot: $error")
                    throw error
                },
            )
        }

        companion object {
            private const val TAG = "My stack: TrackSnapshotRepository"
        }
    }
