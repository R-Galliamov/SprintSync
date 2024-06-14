package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.db.dao.TrackDao
import com.developers.sprintsync.tracking.db.dto.TrackEntity
import com.developers.sprintsync.tracking.model.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl
    @Inject
    constructor(
        private val trackDao: TrackDao,
    ) : TrackRepository {
        override val tracks = trackDao.getAllTracks()

        // CREATE
        override suspend fun saveTrack(track: Track) {
            val entity = TrackEntity.fromDto(track)
            trackDao.insertTrack(entity)
        }

        // READ
        override fun getAllTracks(): Flow<List<Track>> {
            return trackDao.getAllTracks()
        }

        // UPDATE

        // DELETE
    }
