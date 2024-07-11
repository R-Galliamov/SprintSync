package com.developers.sprintsync.tracking.data.di.db

import android.content.Context
import androidx.room.Room
import com.developers.sprintsync.tracking.data.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppDbModule {
    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context,
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db").build()
}
