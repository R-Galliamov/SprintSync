package com.developers.sprintsync.core.components.track.data.data_source

import androidx.collection.LruCache
import com.developers.sprintsync.core.components.track.data.data_source.preparer.TrackPreparer
import com.developers.sprintsync.core.components.track.data.database.dao.TrackDao
import com.developers.sprintsync.core.components.track.data.database.dto.TrackEntity
import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTrackDataSource
    @Inject
    constructor(
        private val trackPreparer: TrackPreparer,
        private val trackDao: TrackDao,
    ) : TrackDataSource {
        private val cache = LruCache<Int, Track>(CACHE_SIZE)
        private val mutex = Mutex()

        private var lastTrackId: Int? = null

        override val tracks = trackDao.getAllTracks()

        override suspend fun saveTrack(track: Track): Int {
            val preparedTrack = trackPreparer.prepareForSave(track)
            val entity = TrackEntity.fromDto(preparedTrack)
            val trackId = trackDao.insertTrack(entity).toInt()
            mutex.withLock { cache.put(trackId, preparedTrack) }
            lastTrackId = trackId
            return trackId
        }

        override suspend fun getLastTrack(): Track? = lastTrackId?.let { getTrackById(it) }

        override suspend fun getTrackById(id: Int): Track =
            mutex.withLock {
                cache[id] ?: trackDao.getTrackById(id).also {
                    cache.put(id, it)
                }
            }

        override suspend fun deleteTrackById(id: Int) {
            trackDao.deleteTrackById(id)
            mutex.withLock { cache.remove(id) }
        }

        companion object {
            private const val CACHE_SIZE = 1
        }
    }
