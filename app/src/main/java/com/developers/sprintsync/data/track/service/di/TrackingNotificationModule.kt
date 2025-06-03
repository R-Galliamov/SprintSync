package com.developers.sprintsync.data.track.service.di

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.R
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationConfig
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationDataProvider
import com.developers.sprintsync.data.track.service.notification.TrackingNotificationDataProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
abstract class TrackingNotificationBindingModule {
    @Binds
    @ServiceScoped
    abstract fun bindTrackingNotificationDataProvider(impl: TrackingNotificationDataProviderImpl): TrackingNotificationDataProvider
}

@Module
@InstallIn(ServiceComponent::class)
class TrackingNotificationModule {

    @Provides
    @ServiceScoped
    fun provideNotificationConfig(): TrackingNotificationConfig =
        object : TrackingNotificationConfig() {
            override val notificationId = 1
            override val channelId = CHANNEL_ID
            override val channelName = CHANNEL_NAME
        }

    @Provides
    @ServiceScoped
    fun provideNotificationManager(
        @ApplicationContext app: Context,
    ): NotificationManager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @ServiceScoped
    fun provideRemoteViews(
        @ApplicationContext app: Context,
    ): RemoteViews = RemoteViews(app.packageName, R.layout.service_notification_layout)

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(
        @ApplicationContext app: Context,
        notificationLayout: RemoteViews,
        config: TrackingNotificationConfig,
    ): NotificationCompat.Builder =
        NotificationCompat
            .Builder(app, config.channelId)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.im_run)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)

    @Provides
    @ServiceScoped
    fun provideNotification(builder: NotificationCompat.Builder): Notification = builder.build()

    companion object {
        private const val CHANNEL_ID = "tracking_channel"
        private const val CHANNEL_NAME = "Tracking location"
    }
}
