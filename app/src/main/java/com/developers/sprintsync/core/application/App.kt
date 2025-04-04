package com.developers.sprintsync.core.application

import android.app.Application
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var trackPreviewRepository: TrackPreviewRepository

    override fun onCreate() {
        super.onCreate()

        // Remove track preview files associated with deleted tracks
        CoroutineScope(Dispatchers.IO).launch {
            trackPreviewRepository.cleanOrphanFiles()
        }
    }
}
