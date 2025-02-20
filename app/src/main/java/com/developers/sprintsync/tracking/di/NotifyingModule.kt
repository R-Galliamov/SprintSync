package com.developers.sprintsync.tracking.di

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.developers.sprintsync.R
import com.developers.sprintsync.tracking.service.notification.TrackingNotificationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class NotifyingModule {
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

    // TODO create a bigContentView
    // TODO change the icon

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
            .setSmallIcon(R.drawable.ic_run_48dp)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)

    @Provides
    @ServiceScoped
    fun provideNotification(builder: NotificationCompat.Builder): Notification = builder.build()
}
