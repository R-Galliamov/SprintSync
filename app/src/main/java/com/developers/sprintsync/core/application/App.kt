package com.developers.sprintsync.core.application

import android.app.Application
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.log.TimberConfigurator
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

    @Inject
    lateinit var timberConfigurator: TimberConfigurator

    @Inject
    lateinit var appLogger: AppLogger


    override fun onCreate() {
        super.onCreate()

        timberConfigurator.setupTimber()
        appLogger.i("Test message")

        // Remove track preview files associated with deleted tracks during previous session
        CoroutineScope(Dispatchers.IO).launch {
            trackPreviewRepository.cleanOrphanFiles()
        }
    }
}
