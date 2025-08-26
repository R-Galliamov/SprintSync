package com.developers.sprintsync.data.track.service.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.calculator.DefaultDistanceCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.pace.DistanceBufferedPaceAnalyzer
import com.developers.sprintsync.data.track.service.processing.calculator.DistanceCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.pace.PaceCalculator
import com.developers.sprintsync.data.track.service.processing.calculator.pace.SmoothedPaceCalculator
import com.developers.sprintsync.data.track.service.processing.segment.DefaultSegmentBuilder
import com.developers.sprintsync.data.track.service.processing.segment.SegmentBuilder
import com.developers.sprintsync.data.track.service.provider.DurationProvider
import com.developers.sprintsync.data.track.service.provider.DurationProviderImpl
import com.developers.sprintsync.data.track.service.provider.LocationProvider
import com.developers.sprintsync.data.track.service.provider.LocationProviderImpl
import com.developers.sprintsync.presentation.main.MainActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ServiceComponent::class)
object CalculationsModule {

    @Provides
    fun provideSmoothedPaceCalculator(paceCalculator: PaceCalculator): SmoothedPaceCalculator {
        val windowSize = 10
        return SmoothedPaceCalculator(paceCalculator, windowSize)
    }

    @Provides
    fun provideCurrentPaceAnalyzer(
        paceCalculator: PaceCalculator,
        smoothedPaceCalculator: SmoothedPaceCalculator,
        log: AppLogger
    ): DistanceBufferedPaceAnalyzer {
        val bufferDistanceMeters = 10f
        return DistanceBufferedPaceAnalyzer(paceCalculator, smoothedPaceCalculator, log, bufferDistanceMeters)
    }
}

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceBindingModule {
    @Binds
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    @Binds
    abstract fun bindDurationProvider(impl: DurationProviderImpl): DurationProvider

    @Binds
    abstract fun bindDistanceCalculator(impl: DefaultDistanceCalculator): DistanceCalculator

    @Binds
    abstract fun bindSegmentBuilder(impl: DefaultSegmentBuilder): SegmentBuilder
}

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    fun provideScope(): CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context,
    ): PendingIntent =
        PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java).also {
                it.action = "Show_tracking_fragment"
            },
            PendingIntent.FLAG_UPDATE_CURRENT,
        )

    /*
    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(
        app,
        TrackingService.NOTIFICATION_CHANNEL_ID
    )
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_run_48dp)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

     */
}

