package com.developers.sprintsync.core.application

import android.app.Application
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.TimberConfigurator
import com.developers.sprintsync.data.track_preview.repository.TrackPreviewRepository
import com.developers.sprintsync.data.track_preview.repository.LocalFileDbTrackPreviewRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application class for initializing app-wide components
 * TODO cover goal-feature with logs and comments
 * TODO add error handler for ui
 * TODO if Service not started - stop location update
 */
@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var trackPreviewRepository: TrackPreviewRepository

    @Inject
    lateinit var timberConfigurator: TimberConfigurator

    @Inject
    lateinit var log: AppLogger


    override fun onCreate() {
        super.onCreate()
        timberConfigurator.setupTimber()
        log.i("Timber setup")

        cleanDeletedTrackPreviewsFiles()
    }

    // Remove track preview files associated with deleted tracks during previous session
    private fun cleanDeletedTrackPreviewsFiles() {
        when (val impl = trackPreviewRepository) {
            is LocalFileDbTrackPreviewRepository -> CoroutineScope(Dispatchers.IO).launch { impl.cleanOrphanFiles() }
        }
    }
}
