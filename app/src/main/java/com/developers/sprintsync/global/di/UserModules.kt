package com.developers.sprintsync.global.di

import com.developers.sprintsync.user.model.UserSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserSettingsModules {
    @Provides
    @Singleton
    fun provideUserSettings(): UserSettings = UserSettings(60.0)
}
