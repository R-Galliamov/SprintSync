package com.developers.sprintsync.parameters.dataStorage.di

import com.developers.sprintsync.parameters.model.UserSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserSettingsModule {
    companion object {
        const val USER_TEST_WEIGHT = 60.0f
    }

    @Provides
    @Singleton
    fun provideUserSettings(): UserSettings = UserSettings(USER_TEST_WEIGHT)
}
