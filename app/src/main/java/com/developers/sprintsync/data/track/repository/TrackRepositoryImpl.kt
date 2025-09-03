package com.developers.sprintsync.data.track.repository

import androidx.collection.LruCache
import com.developers.sprintsync.core.util.log.AppLogger
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
    private val log: AppLogger,
) : TrackRepository {
    private val cache = LruCache<Int, Track>(CACHE_SIZE)
    private val mutex = Mutex()

    private var lastTrackId: Int? = null

    override val tracksFlow = trackDao.getAllTracks()

    override suspend fun save(track: Track): Int {
        val entity = TrackEntity.fromDto(track)
        val trackId = trackDao.insertTrack(entity).toInt()
        mutex.withLock { cache.put(trackId, track) }
        lastTrackId = trackId
        log.i("Track saved and cached: id=$trackId")
        return trackId
    }

    override suspend fun getLastTrack(): Track? {
        log.i("Fetching last track. lastTrackId=$lastTrackId")
        return lastTrackId?.let { getTrackById(it) }
    }

    override suspend fun getTrackById(id: Int): Track = mutex.withLock {
        val cached = cache[id]
        if (cached != null) {
            log.i("Track retrieved from cache: id=$id")
            return cached
        }

        val track = trackDao.getTrackById(id)
        cache.put(id, track)
        log.i("Track retrieved from database and cached: id=$id")
        return track
    }

    override suspend fun deleteTrackById(id: Int) {
        trackDao.deleteTrackById(id)
        mutex.withLock { cache.remove(id) }
        log.i("Track deleted from database and cache: id=$id")
    }

    companion object {
        private const val CACHE_SIZE = 3
    }
}
