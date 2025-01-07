package com.developers.sprintsync.core.application.di.database

import android.content.Context
import androidx.room.Room
import com.developers.sprintsync.core.database.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context,
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db").build()
}
