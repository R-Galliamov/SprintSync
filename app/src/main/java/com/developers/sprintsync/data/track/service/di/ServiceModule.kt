package com.developers.sprintsync.data.track.service.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.developers.sprintsync.data.track.service.processing.segment.SegmentGenerator
import com.developers.sprintsync.data.track.service.processing.segment.SegmentGeneratorImpl
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
abstract class ServiceBindingModule {
    @Binds
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    @Binds
    abstract fun bindDurationProvider(impl: DurationProviderImpl): DurationProvider

    @Binds
    abstract fun bindSegmentGenerator(impl: SegmentGeneratorImpl): SegmentGenerator
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

