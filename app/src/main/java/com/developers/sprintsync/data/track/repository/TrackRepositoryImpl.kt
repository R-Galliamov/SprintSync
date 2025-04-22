package com.developers.sprintsync.data.track.repository

import androidx.collection.LruCache
import com.developers.sprintsync.data.track.database.dao.TrackDao
import com.developers.sprintsync.data.track.database.dto.TrackEntity
import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl
@Inject
constructor(
    private val trackDao: TrackDao,
) : TrackRepository {
    private val cache = LruCache<Int, Track>(CACHE_SIZE)
    private val mutex = Mutex()

    private var lastTrackId: Int? = null

    override val tracks = trackDao.getAllTracks()

    override suspend fun saveTrack(track: Track): Int {
        val entity = TrackEntity.fromDto(track)
        val trackId = trackDao.insertTrack(entity).toInt()
        mutex.withLock { cache.put(trackId, track) }
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
