package com.developers.sprintsync.core.components.track.data.repository

import androidx.collection.LruCache
import com.developers.sprintsync.core.components.track.data.database.dao.TrackDao
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track.data.model.Track
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
