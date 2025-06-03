package com.developers.sprintsync.data.user_parameters.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepository
import com.developers.sprintsync.data.user_parameters.repository.UserParametersRepositoryImpl
import com.developers.sprintsync.data.user_parameters.source.LocalUserParametersDataSource
import com.developers.sprintsync.data.user_parameters.source.UserParametersDataSource
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import com.developers.sprintsync.domain.user_parameters.use_case.UserParametersUseCase
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserParametersStorageModule {

    @Provides
    @Singleton
    fun provideUserParameters(useCase: UserParametersUseCase): UserParameters = runBlocking { useCase.invoke().first() }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(PREFERENCES_KEY)
        }

    @Provides
    @Singleton
    @Named(LOCAL_STORAGE_KEY)
    fun provideLocalDataSource(dataStore: DataStore<Preferences>, log: AppLogger): UserParametersDataSource =
        LocalUserParametersDataSource(dataStore, GsonBuilder().create(), log)

    @Provides
    @Singleton
    fun provideUserParametersRepository(
        @Named(LOCAL_STORAGE_KEY) localDataSource: UserParametersDataSource,
        log: AppLogger
    ): UserParametersRepository = UserParametersRepositoryImpl(localDataSource, log)

    private companion object {
        const val PREFERENCES_KEY = "user_prefs"
        const val LOCAL_STORAGE_KEY = "local"
    }
}
