package com.developers.sprintsync.tracking.dataStorage.repository.track

import androidx.collection.LruCache
import com.developers.sprintsync.tracking.dataStorage.db.dao.TrackDao
import com.developers.sprintsync.tracking.dataStorage.db.dto.TrackEntity
import com.developers.sprintsync.tracking.session.model.track.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl
    @Inject
    constructor(
        private val trackDao: TrackDao,
    ) : TrackRepository {
        private val cache = LruCache<Int, Track>(CACHE_SIZE)

        override val tracks = trackDao.getAllTracks()

        // CREATE
        override suspend fun saveTrack(track: Track) {
            val entity = TrackEntity.fromDto(track)
            trackDao.insertTrack(entity)
        }

        // READ
        override fun getTrackById(id: Int): Track =
            cache[id] ?: trackDao.getTrackById(id).also {
                cache.put(id, it)
            }

        // DELETE
        override suspend fun deleteTrackById(id: Int) {
            cache.remove(id)
            trackDao.deleteTrackById(id)
        }

        companion object {
            private const val CACHE_SIZE = 1
        }
    }
